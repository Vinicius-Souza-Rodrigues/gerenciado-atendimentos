package com.agendamentos.adapter.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioDisponivelResponse(
    @JsonProperty("id") UUID id,
    @JsonProperty("dia_semana") String diaDaSemana,
    @JsonProperty("hora_inicio") LocalTime horaInicio,
    @JsonProperty("hora_fim") LocalTime horaFim,
    @JsonProperty("ativo") boolean ativo
) {}
