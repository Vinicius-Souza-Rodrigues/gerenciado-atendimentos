package com.agendamentos.adapter.inbound.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookMensagemRequest(
        String instance,
        String phone,
        String message,
        String sender,
        String timestamp
) {
}
