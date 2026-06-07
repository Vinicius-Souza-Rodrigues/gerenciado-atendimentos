package com.agendamentos.adapter.inbound.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nome,
        String telefone,
        LocalDateTime criadoEm
) {
}
