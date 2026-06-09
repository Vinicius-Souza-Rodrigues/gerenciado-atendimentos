package com.agendamentos.adapter.inbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TelegramMensagemRequest(
    @JsonProperty("update_id") Long updateId,
    Message message
) {
    public record Message(
        @JsonProperty("message_id") Long messageId,
        Chat chat,
        @JsonProperty("date") Long date,
        String text
    ) {
        public record Chat(
            @JsonProperty("id") Long id,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("type") String type
        ) {}
    }
}
