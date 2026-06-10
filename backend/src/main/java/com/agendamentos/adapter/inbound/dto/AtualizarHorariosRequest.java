package com.agendamentos.adapter.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.List;

public record AtualizarHorariosRequest(
    @JsonProperty("horarios") List<HorarioSlotRequest> horarios
) {
    public record HorarioSlotRequest(
        @JsonProperty("dia_semana") String diaDaSemana,
        @JsonProperty("hora_inicio") LocalTime horaInicio,
        @JsonProperty("hora_fim") LocalTime horaFim,
        @JsonProperty("ativo") boolean ativo
    ) {}
}
