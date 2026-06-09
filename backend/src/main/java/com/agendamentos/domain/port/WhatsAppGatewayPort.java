package com.agendamentos.domain.port;

public interface WhatsAppGatewayPort {
    void enviarMensagem(String telefone, String mensagem);
    boolean verificarConexao();
}
