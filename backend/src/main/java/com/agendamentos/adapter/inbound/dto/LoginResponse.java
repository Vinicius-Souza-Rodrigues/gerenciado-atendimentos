package com.agendamentos.adapter.inbound.dto;

import java.util.UUID;

public record LoginResponse(String token, UUID prestadorId, String nomeNegocio) {
}
