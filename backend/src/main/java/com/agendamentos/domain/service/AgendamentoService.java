package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.exception.*;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.port.NotificacaoPort;
import com.agendamentos.domain.valueobject.NomeServico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
    
    private final AgendamentoRepositoryPort agendamentoRepository;
    private final NotificacaoPort notificacaoPort;

    public Agendamento criar(UUID clienteId, UUID prestadorId, LocalDateTime dataHora, NomeServico servico) {
        validarAgendamento(clienteId, prestadorId, dataHora);
        verificarConflitoDHorario(prestadorId, dataHora);
        
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, dataHora, servico)
            .confirmar();
        
        agendamentoRepository.salvar(agendamento);
        
        return agendamento;
    }

    public Agendamento cancelar(UUID agendamentoId) {
        throw new UnsupportedOperationException("Implementar após portas estarem prontas");
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
}