package com.agendamentos.adapter.outbound.gateway.dto;

public record EnviarMensagemRequest(
        String telefone,
        String mensagem
) {
}
