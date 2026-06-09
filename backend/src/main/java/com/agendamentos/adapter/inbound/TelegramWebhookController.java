package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.TelegramMensagemRequest;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.service.AgendamentoMessageParser;
import com.agendamentos.domain.service.AgendamentoService;
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
    private final ClienteRepositoryPort clienteRepository;
    private final PrestadorRepositoryPort prestadorRepository;

    @PostMapping("/{prestadorId}")
    public ResponseEntity<Void> receberMensagem(
            @PathVariable UUID prestadorId,
            @RequestBody TelegramMensagemRequest request) {
        try {
            if (request.message() == null || request.message().text() == null) {
                log.warn("Mensagem vazia recebida");
                return ResponseEntity.ok().build();
            }

            var chatId = request.message().chat().id().toString();
            var nomeCliente = request.message().chat().firstName();
            var mensagem = request.message().text();

            log.info("Mensagem recebida do chat {} ({}): {}", chatId, nomeCliente, mensagem);

            var prestador = prestadorRepository.buscarPorId(prestadorId)
                    .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

            var tipoComando = messageParser.identificarComando(mensagem);

            switch (tipoComando) {
                case MARCAR_AGENDAMENTO:
                    processarMarcarAgendamento(chatId, nomeCliente, prestadorId, mensagem);
                    break;
                case LISTAR_HORARIOS:
                    processarListarHorarios(chatId, prestador.getNomeNegocio());
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

            // AgendamentoService.criar já dispara notificarAgendamentoCriado via TelegramService
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

    private void processarListarHorarios(String chatId, String nomeNegocio) {
        log.info("Cliente pediu horários disponíveis");
        telegramService.enviarMensagem(chatId,
                mensagensService.mensagemHorariosDisponiveis(
                        java.util.List.of("09:00", "10:30", "14:00", "15:30"),
                        nomeNegocio
                ));
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
            log.info("Cliente {} solicitou cancelamento", nomeCliente);
            telegramService.enviarMensagem(chatId,
                    mensagensService.mensagemCancelamento("Seu agendamento"));
        } catch (Exception e) {
            log.error("Erro ao processar cancelamento", e);
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
                    // chatId é numérico — padding para 11 dígitos para satisfazer validação de Telefone
                    String telefonePlaceholder = String.format("%011d", Math.abs(Long.parseLong(chatId)));
                    var novoCliente = new Cliente(nomeCliente, new Telefone(telefonePlaceholder), chatId);
                    return clienteRepository.salvar(novoCliente);
                });
    }
}
