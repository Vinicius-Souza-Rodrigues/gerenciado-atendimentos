package com.agendamentos.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "horarios_disponiveis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDisponivelEntity {

    @Id
    private UUID id;

    @Column(name = "prestador_id", nullable = false)
    private UUID prestadorId;

    @Column(name = "dia_da_semana", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek diaDaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;
}
