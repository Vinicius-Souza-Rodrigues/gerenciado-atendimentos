package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.WebhookMensagemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    @PostMapping("/whatsapp/messages")
    public ResponseEntity<Void> receberMensagem(@RequestBody WebhookMensagemRequest request) {
        try {
            log.info("Webhook recebido - Instância: {}, Telefone: {}, Mensagem: {}",
                    request.instance(), request.sender(), request.message());

            // TODO: Processar mensagem recebida
            // Exemplos de casos de uso:
            // 1. Cliente confirmando agendamento
            // 2. Cliente cancelando agendamento
            // 3. Cliente enviando feedback

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/whatsapp/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check do webhook WhatsApp");
        return ResponseEntity.ok("Webhook WhatsApp está ativo");
    }
}
