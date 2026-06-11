package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.HorarioDisponivel;
import com.agendamentos.domain.port.HorarioDisponivelRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioDisponivelService {

    private final HorarioDisponivelRepositoryPort horarioRepository;

    public List<HorarioDisponivel> listarHorariosPrestador(UUID prestadorId) {
        return horarioRepository.listarPorPrestador(prestadorId);
    }

    @Transactional
    public List<HorarioDisponivel> atualizarHorariosPrestador(UUID prestadorId, List<HorarioDisponivelDTO> horarios) {
        horarioRepository.deletarTodosPorPrestador(prestadorId);

        return horarios.stream()
            .map(dto -> {
                var horarioDisponivel = new HorarioDisponivel(
                    prestadorId,
                    dto.diaDaSemana(),
                    dto.horaInicio(),
                    dto.horaFim()
                );
                if (!dto.ativo()) {
                    horarioDisponivel = horarioDisponivel.desativar();
                }
                return horarioRepository.salvar(horarioDisponivel);
            })
            .toList();
    }

    public record HorarioDisponivelDTO(
        DayOfWeek diaDaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        boolean ativo
    ) {}
}
