package com.agendamentos.domain.port;

public interface TelegramGatewayPort {
    void enviarMensagem(String chatId, String mensagem);
    boolean verificarConexao();
}
