package com.agendamentos.domain.service;

import com.agendamentos.domain.exception.AgendamentoPassadoException;
import com.agendamentos.domain.exception.ConflitoDHorarioException;
import com.agendamentos.domain.model.Agendamento;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.UUID;

public class AgendamentoService {

    private final AgendamentoRepositoryPort agendamentoRepo;

    public AgendamentoService(AgendamentoRepositoryPort agendamentoRepo) {
        this.agendamentoRepo = agendamentoRepo;
    }

    public Agendamento criar(UUID clienteId, UUID prestadorId, LocalDateTime dataHora, String servico) {
        if (!dataHora.isAfter(LocalDateTime.now())) {
            throw new AgendamentoPassadoException("Agendamento deve ser para uma data futura");
        }

        var conflitos = agendamentoRepo.listarConfirmadosPorPrestadorEPeriodo(prestadorId, dataHora, dataHora);
        if (!conflitos.isEmpty()) {
            throw new ConflitoDHorarioException("Já existe agendamento confirmado neste horário");
        }

        var agendamento = Agendamento.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .prestadorId(prestadorId)
                .dataHora(dataHora)
                .servico(new NomeServico(servico))
                .status(StatusAgendamento.PENDENTE)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return agendamentoRepo.salvar(agendamento);
    }

    public Agendamento cancelar(UUID agendamentoId) {
        var agendamento = agendamentoRepo.buscarPorId(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado: " + agendamentoId));

        var cancelado = agendamento.cancelar();
        return agendamentoRepo.salvar(cancelado);
    }

}
