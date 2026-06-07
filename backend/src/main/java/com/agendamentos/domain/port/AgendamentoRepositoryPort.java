package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.Agendamento;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;

public interface AgendamentoRepositoryPort {
    Agendamento salvar(Agendamento agendamento);
    Optional<Agendamento> buscarPorId(UUID id);
    List<Agendamento> listarTodos();
    List<Agendamento> listarPorPrestador(UUID prestadorId);
    List<Agendamento> listarPorPrestadorEData(UUID prestadorId, LocalDateTime data);
    List<Agendamento> listarPorCliente(UUID clienteId);
}