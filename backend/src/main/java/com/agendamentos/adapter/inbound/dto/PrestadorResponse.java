package com.agendamentos.adapter.inbound.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PrestadorResponse(
        UUID id,
        String nomeNegocio,
        String telefoneWhatsApp,
        LocalDateTime criadoEm
) {
}
