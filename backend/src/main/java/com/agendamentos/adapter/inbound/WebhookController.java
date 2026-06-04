package com.agendamentos.adapter.inbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/mensagem")
    public ResponseEntity<Map<String, String>> receberMensagem(@RequestBody Map<String, Object> payload) {
        log.info("Webhook recebido: {}", payload);

        // Fase 1: apenas receber e logar
        // Implementação da lógica será feita na Fase 4

        return ResponseEntity.ok(Map.of("status", "recebido"));
    }

}
