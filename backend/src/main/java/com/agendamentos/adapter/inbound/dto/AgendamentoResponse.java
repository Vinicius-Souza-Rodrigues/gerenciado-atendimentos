package com.agendamentos.adapter.inbound.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id,
        UUID clienteId,
        UUID prestadorId,
        LocalDateTime dataHora,
        String servico,
        String status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
