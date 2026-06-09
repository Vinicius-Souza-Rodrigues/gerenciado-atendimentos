package com.agendamentos.adapter.outbound.gateway;

import com.agendamentos.domain.port.TelegramGatewayPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramGateway implements TelegramGatewayPort {

    private final transient RestTemplate restTemplate;
    private final transient ObjectMapper objectMapper;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.enabled:false}")
    private boolean telegramEnabled;

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Override
    public void enviarMensagem(String chatId, String mensagem) {
        if (!telegramEnabled) {
            log.warn("Telegram desabilitado. Mensagem não será enviada para chat {}", chatId);
            return;
        }

        try {
            if (botToken == null || botToken.isBlank()) {
                log.warn("Token do bot Telegram não configurado. Simular envio para chat {}", chatId);
                return;
            }

            String url = TELEGRAM_API_URL + botToken + "/sendMessage";

            Map<String, Object> request = new HashMap<>();
            request.put("chat_id", chatId);
            request.put("text", mensagem);
            request.put("parse_mode", "HTML");

            log.info("Enviando mensagem Telegram para chat {} via API Telegram", chatId);

            restTemplate.postForObject(url, request, String.class);

            log.info("Mensagem Telegram enviada com sucesso para chat {}", chatId);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem Telegram para chat {}: {}", chatId, e.getMessage());
            throw new RuntimeException("Falha ao enviar mensagem Telegram", e);
        }
    }

    @Override
    public boolean verificarConexao() {
        if (!telegramEnabled) {
            log.warn("Telegram desabilitado");
            return false;
        }

        try {
            if (botToken == null || botToken.isBlank()) {
                log.warn("Token do bot Telegram não configurado");
                return false;
            }

            String url = TELEGRAM_API_URL + botToken + "/getMe";

            log.info("Verificando conexão com API Telegram");

            restTemplate.getForObject(url, String.class);

            log.info("Conexão com API Telegram verificada com sucesso");
            return true;
        } catch (Exception e) {
            log.error("Erro ao verificar conexão com API Telegram: {}", e.getMessage());
            return false;
        }
    }
}
