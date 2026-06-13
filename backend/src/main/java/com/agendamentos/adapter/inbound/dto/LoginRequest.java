package com.agendamentos.adapter.inbound.dto;

import java.util.UUID;

public record LoginRequest(UUID prestadorId, String senha) {
}
