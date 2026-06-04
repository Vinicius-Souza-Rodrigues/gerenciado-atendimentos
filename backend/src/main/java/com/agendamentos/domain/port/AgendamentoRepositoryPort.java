package com.agendamentos.domain.port;

import com.agendamentos.domain.model.Agendamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepositoryPort {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(UUID id);

    List<Agendamento> listarConfirmadosPorPrestadorEPeriodo(UUID prestadorId, LocalDateTime inicio, LocalDateTime fim);

    List<Agendamento> listarPorPrestadorEData(UUID prestadorId, LocalDate data);

}
