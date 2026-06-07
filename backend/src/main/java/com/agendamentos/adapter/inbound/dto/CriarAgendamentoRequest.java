package com.agendamentos.adapter.inbound.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarAgendamentoRequest(
        UUID clienteId,
        UUID prestadorId,
        LocalDateTime dataHora,
        String servico
) {
}
