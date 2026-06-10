package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.exception.*;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.valueobject.NomeServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendamentoService {

    private final AgendamentoRepositoryPort agendamentoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final PrestadorRepositoryPort prestadorRepository;
    private final WhatsAppService whatsAppService;
    private final TelegramService telegramService;

    public Agendamento criar(UUID clienteId, UUID prestadorId, LocalDateTime dataHora, NomeServico servico) {
        validarAgendamento(clienteId, prestadorId, dataHora);
        verificarConflitoDHorario(prestadorId, dataHora);

        Agendamento agendamento = new Agendamento(clienteId, prestadorId, dataHora, servico)
            .confirmar();

        var agendamentoSalvo = agendamentoRepository.salvar(agendamento);

        enviarNotificacoes(agendamentoSalvo, clienteId, prestadorId);

        return agendamentoSalvo;
    }

    public Agendamento cancelar(UUID agendamentoId) {
        var agendamento = agendamentoRepository.buscarPorId(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));

        var agendamentoCancelado = agendamento.cancelar();
        var agendamentoSalvo = agendamentoRepository.salvar(agendamentoCancelado);

        try {
            var cliente = clienteRepository.buscarPorId(agendamento.getClienteId()).orElse(null);
            if (cliente != null) {
                telegramService.notificarStatusAtualizado(agendamentoSalvo, cliente);
                whatsAppService.notificarStatusAtualizado(agendamentoSalvo, cliente);
            }
        } catch (Exception e) {
            log.error("Erro ao enviar notificações de cancelamento para agendamento {}: {}", agendamentoId, e.getMessage());
        }

        return agendamentoSalvo;
    }

    private void validarAgendamento(UUID clienteId, UUID prestadorId, LocalDateTime dataHora) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID não pode ser nulo");
        }
        if (prestadorId == null) {
            throw new IllegalArgumentException("Prestador ID não pode ser nulo");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("Data/hora não pode ser nula");
        }
        if (!dataHora.isAfter(LocalDateTime.now())) {
            throw new AgendamentoNoPassadoException("Agendamento deve ser no futuro");
        }
    }

    private void verificarConflitoDHorario(UUID prestadorId, LocalDateTime dataHora) {
        var agendamentosNoHorario = agendamentoRepository.listarPorPrestadorEData(prestadorId, dataHora);

        if (!agendamentosNoHorario.isEmpty()) {
            throw new ConflitoDHorarioException("Horário indisponível para este prestador");
        }
    }

    private void enviarNotificacoes(Agendamento agendamento, UUID clienteId, UUID prestadorId) {
        try {
            var cliente = clienteRepository.buscarPorId(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            var prestador = prestadorRepository.buscarPorId(prestadorId)
                    .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

            whatsAppService.notificarAgendamentoCriado(agendamento, cliente, prestador);
            telegramService.notificarAgendamentoCriado(agendamento, cliente, prestador);
        } catch (Exception e) {
            log.error("Erro ao enviar notificações para agendamento {}: {}", agendamento.getId(), e.getMessage());
        }
    }
}