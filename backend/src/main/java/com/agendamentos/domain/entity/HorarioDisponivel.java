package com.agendamentos.domain.entity;

import lombok.Value;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Value
public class HorarioDisponivel {
    UUID id;
    UUID prestadorId;
    DayOfWeek diaDaSemana;
    LocalTime horaInicio;
    LocalTime horaFim;
    boolean ativo;

    public HorarioDisponivel(UUID prestadorId, DayOfWeek diaDaSemana, LocalTime horaInicio, LocalTime horaFim) {
        if (prestadorId == null) {
            throw new IllegalArgumentException("Prestador ID não pode ser nulo");
        }
        if (diaDaSemana == null) {
            throw new IllegalArgumentException("Dia da semana não pode ser nulo");
        }
        if (horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Horas não podem ser nulas");
        }
        if (!horaInicio.isBefore(horaFim)) {
            throw new IllegalArgumentException("Hora de início deve ser antes da hora de fim");
        }

        this.id = UUID.randomUUID();
        this.prestadorId = prestadorId;
        this.diaDaSemana = diaDaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.ativo = true;
    }

    private HorarioDisponivel(UUID id, UUID prestadorId, DayOfWeek diaDaSemana,
                             LocalTime horaInicio, LocalTime horaFim, boolean ativo) {
        this.id = id;
        this.prestadorId = prestadorId;
        this.diaDaSemana = diaDaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.ativo = ativo;
    }

    public static HorarioDisponivel comId(UUID id, UUID prestadorId, DayOfWeek diaDaSemana,
                                          LocalTime horaInicio, LocalTime horaFim, boolean ativo) {
        return new HorarioDisponivel(id, prestadorId, diaDaSemana, horaInicio, horaFim, ativo);
    }

    public HorarioDisponivel desativar() {
        return new HorarioDisponivel(this.id, this.prestadorId, this.diaDaSemana,
                                    this.horaInicio, this.horaFim, false);
    }

    public HorarioDisponivel ativar() {
        return new HorarioDisponivel(this.id, this.prestadorId, this.diaDaSemana,
                                    this.horaInicio, this.horaFim, true);
    }
}
