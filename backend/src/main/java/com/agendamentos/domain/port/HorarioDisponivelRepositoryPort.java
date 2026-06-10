package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.HorarioDisponivel;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HorarioDisponivelRepositoryPort {
    HorarioDisponivel salvar(HorarioDisponivel horario);
    Optional<HorarioDisponivel> buscarPorId(UUID id);
    List<HorarioDisponivel> listarPorPrestador(UUID prestadorId);
    List<HorarioDisponivel> listarPorPrestadorEDia(UUID prestadorId, DayOfWeek dia);
    void deletarPorId(UUID id);
    void deletarTodosPorPrestador(UUID prestadorId);
}
