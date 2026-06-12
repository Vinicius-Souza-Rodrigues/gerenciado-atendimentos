package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.TelegramMensagemRequest;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.port.TelegramContextoPort;
import com.agendamentos.domain.service.AgendamentoMessageParser;
import com.agendamentos.domain.service.AgendamentoService;
import com.agendamentos.domain.service.HorarioDisponivelService;
import com.agendamentos.domain.service.MensagensPersonalizadasService;
import com.agendamentos.domain.service.TelegramService;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/webhook/telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookController {

    private final AgendamentoMessageParser messageParser;
    private final MensagensPersonalizadasService mensagensService;
    private final TelegramService telegramService;
    private final AgendamentoService agendamentoService;
    private final HorarioDisponivelService horarioService;
    private final AgendamentoRepositoryPort agendamentoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final PrestadorRepositoryPort prestadorRepository;
    private final TelegramContextoPort contextoPort;

    @PostMapping
    public ResponseEntity<Void> receberMensagem(@RequestBody TelegramMensagemRequest request) {
        try {
            if (request.message() == null || request.message().text() == null) {
                log.warn("Mensagem vazia recebida");
                return ResponseEntity.ok().build();
            }

            var chatId = request.message().chat().id().toString();
            var nomeCliente = request.message().chat().firstName() != null
                    ? request.message().chat().firstName()
                    : "Usuário " + chatId.substring(chatId.length() - 4);
            var mensagem = request.message().text().trim();

            log.info("Mensagem recebida do chat {} ({}): {}", chatId, nomeCliente, mensagem);

            if (mensagem.startsWith("/start")) {
                processarInicio(chatId, nomeCliente, mensagem);
                return ResponseEntity.ok().build();
            }

            var prestadorIdOpt = contextoPort.buscarPrestadorId(chatId);
            if (prestadorIdOpt.isEmpty()) {
                telegramService.enviarMensagem(chatId,
                        "<b>Olá! 👋</b>\n\nPara começar, use o link do estabelecimento que você deseja agendar.");
                return ResponseEntity.ok().build();
            }

            var prestadorId = prestadorIdOpt.get();
            var prestador = prestadorRepository.buscarPorId(prestadorId)
                    .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

            var tipoComando = messageParser.identificarComando(mensagem);

            switch (tipoComando) {
                case MARCAR_AGENDAMENTO:
                    processarMarcarAgendamento(chatId, nomeCliente, prestadorId, mensagem);
                    break;
                case LISTAR_HORARIOS:
                    processarListarHorarios(chatId, prestador.getNomeNegocio(), prestadorId);
                    break;
                case AVISAR_CHEGADA:
                    processarChegada(chatId, nomeCliente, prestadorId);
                    break;
                case CANCELAR_AGENDAMENTO:
                    processarCancelamento(chatId, nomeCliente, prestadorId);
                    break;
                case DESCONHECIDO:
                default:
                    responderComandoDesconhecido(chatId, prestador.getNomeNegocio());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook Telegram", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void processarInicio(String chatId, String nomeCliente, String mensagem) {
        var partes = mensagem.split(" ");
        if (partes.length < 2) {
            telegramService.enviarMensagem(chatId,
                    "<b>Olá! 👋</b>\n\nUse o link do estabelecimento para começar.");
            return;
        }
        try {
            var prestadorId = UUID.fromString(partes[1]);
            var prestador = prestadorRepository.buscarPorId(prestadorId).orElse(null);
            if (prestador == null) {
                telegramService.enviarMensagem(chatId, "<b>❌ Link inválido.</b>\n\nVerifique o link e tente novamente.");
                return;
            }
            contextoPort.vincular(chatId, prestadorId);
            log.info("Chat {} vinculado ao prestador {}", chatId, prestadorId);
            telegramService.enviarMensagem(chatId, mensagensService.mensagemBoasVindas(prestador));
        } catch (IllegalArgumentException e) {
            telegramService.enviarMensagem(chatId, "<b>❌ Link inválido.</b>\n\nVerifique o link e tente novamente.");
        }
    }

    private void processarMarcarAgendamento(String chatId, String nomeCliente, UUID prestadorId, String mensagem) {
        try {
            var dados = messageParser.extrairDados(mensagem);

            if (dados == null) {
                telegramService.enviarMensagem(chatId,
                        mensagensService.mensagemErroAgendamento("Não consegui entender a data/hora. Tente: 'Quero marcar corte em 10/06 às 14h'"));
                return;
            }

            var dataHora = messageParser.parseDataHora(dados.data(), dados.hora());
            if (dataHora == null) {
                telegramService.enviarMensagem(chatId,
                        mensagensService.mensagemErroAgendamento("Data ou hora inválida. Use o formato: DD/MM às HH:MM"));
                return;
            }

            var cliente = buscarOuCriarCliente(chatId, nomeCliente);

            agendamentoService.criar(
                    cliente.getId(),
                    prestadorId,
                    dataHora,
                    new NomeServico(dados.servico())
            );

            log.info("Agendamento salvo e confirmação enviada via Telegram para chat {}", chatId);

        } catch (Exception e) {
            log.error("Erro ao processar agendamento", e);
            telegramService.enviarMensagem(chatId,
                    mensagensService.mensagemErroAgendamento(e.getMessage()));
        }
    }

    private void processarListarHorarios(String chatId, String nomeNegocio, UUID prestadorId) {
        try {
            var horariosDisponiveis = gerarHorariosDisponiveis(prestadorId);

            if (horariosDisponiveis.isEmpty()) {
                telegramService.enviarMensagem(chatId,
                        "<b>⏳ Sem horários disponíveis</b>\n\n" +
                        "Não há horários disponíveis nos próximos 7 dias. Tente novamente mais tarde.");
                return;
            }

            log.info("Cliente pediu horários disponíveis - encontrou {}", horariosDisponiveis.size());
            telegramService.enviarMensagem(chatId,
                    mensagensService.mensagemHorariosDisponiveis(horariosDisponiveis, nomeNegocio));
        } catch (Exception e) {
            log.error("Erro ao processar listagem de horários", e);
            telegramService.enviarMensagem(chatId,
                    "<b>❌ Erro ao listar horários</b>\n\nTente novamente.");
        }
    }

    private java.util.List<String> gerarHorariosDisponiveis(UUID prestadorId) {
        var horariosDisponiveis = new java.util.ArrayList<String>();
        var agora = java.time.LocalDateTime.now();
        var horariosConfigurados = horarioService.listarHorariosPrestador(prestadorId);
        var formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM (EEE)", new java.util.Locale("pt", "BR"));

        for (int i = 1; i <= 7; i++) {
            var dia = agora.plusDays(i);
            var diaDaSemana = dia.getDayOfWeek();

            var sessoesNoDia = horariosConfigurados.stream()
                .filter(h -> h.getDiaDaSemana() == diaDaSemana && h.isAtivo())
                .toList();

            for (var sessao : sessoesNoDia) {
                var inicio = dia.withHour(sessao.getHoraInicio().getHour())
                               .withMinute(sessao.getHoraInicio().getMinute())
                               .withSecond(0).withNano(0);
                var fim = dia.withHour(sessao.getHoraFim().getHour())
                            .withMinute(sessao.getHoraFim().getMinute())
                            .withSecond(0).withNano(0);

                if (!agendamentoRepository.existeConflito(prestadorId, inicio, fim)) {
                    horariosDisponiveis.add(
                        dia.format(formatter) + " " + sessao.getHoraInicio() + " - " + sessao.getHoraFim()
                    );
                }
            }
        }
        return horariosDisponiveis;
    }

    private void processarChegada(String chatId, String nomeCliente, UUID prestadorId) {
        try {
            log.info("Cliente {} avisou chegada", nomeCliente);
            telegramService.enviarMensagem(chatId,
                    "<b>✅ Recebido!</b>\n\nO prestador será avisado de sua chegada. Bem-vindo! 👋");
        } catch (Exception e) {
            log.error("Erro ao processar chegada", e);
        }
    }

    private void processarCancelamento(String chatId, String nomeCliente, UUID prestadorId) {
        try {
            var cliente = buscarOuCriarCliente(chatId, nomeCliente);
            var agendamentosDoCliente = agendamentoRepository.listarPorCliente(cliente.getId());

            if (agendamentosDoCliente.isEmpty()) {
                telegramService.enviarMensagem(chatId,
                        "<b>❌ Nenhum agendamento encontrado</b>\n\nVocê não tem agendamentos para cancelar.");
                return;
            }

            var agendamentoParaCancelar = agendamentosDoCliente.stream()
                    .filter(a -> a.estahConfirmado() && a.estahNoFuturo())
                    .min((a, b) -> a.getDataHora().compareTo(b.getDataHora()))
                    .orElse(null);

            if (agendamentoParaCancelar == null) {
                telegramService.enviarMensagem(chatId,
                        "<b>❌ Agendamento indisponível</b>\n\nNão há agendamentos confirmados para cancelar.");
                return;
            }

            agendamentoService.cancelar(agendamentoParaCancelar.getId());

            log.info("Agendamento {} cancelado via Telegram para cliente {}", agendamentoParaCancelar.getId(), chatId);
            telegramService.enviarMensagem(chatId,
                    "<b>✅ Cancelamento Confirmado</b>\n\nSeu agendamento de " +
                    agendamentoParaCancelar.getServico().getValor() +
                    " foi cancelado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao processar cancelamento", e);
            telegramService.enviarMensagem(chatId,
                    "<b>❌ Erro ao cancelar</b>\n\nOcorreu um erro ao processar seu cancelamento. Tente novamente.");
        }
    }

    private void responderComandoDesconhecido(String chatId, String nomeNegocio) {
        telegramService.enviarMensagem(chatId,
                "<b>Desculpa, não entendi! 🤔</b>\n\n" +
                        "Você pode:\n" +
                        "✅ Marcar: 'Quero marcar corte em 10/06 às 14h'\n" +
                        "✅ Ver horários: 'Que horários tem?'\n" +
                        "✅ Avisar chegada: 'Cheguei!'\n" +
                        "✅ Cancelar: 'Cancelar meu agendamento'");
    }

    private Cliente buscarOuCriarCliente(String chatId, String nomeCliente) {
        return clienteRepository.buscarPorTelegramChatId(chatId)
                .orElseGet(() -> {
                    String telefonePlaceholder = String.format("%011d", Math.abs(Long.parseLong(chatId)));
                    var novoCliente = new Cliente(nomeCliente, new Telefone(telefonePlaceholder), chatId);
                    return clienteRepository.salvar(novoCliente);
                });
    }
}
