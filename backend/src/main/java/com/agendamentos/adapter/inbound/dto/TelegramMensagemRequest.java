package com.agendamentos.adapter.inbound.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramMensagemRequest(
    @JsonProperty("update_id") Long updateId,
    @JsonProperty("message") Message message
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
        @JsonProperty("message_id") Long messageId,
        @JsonProperty("chat") Chat chat,
        @JsonProperty("date") Long date,
        @JsonProperty("text") String text
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Chat(
            @JsonProperty("id") Long id,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("type") String type
        ) {}
    }
}
