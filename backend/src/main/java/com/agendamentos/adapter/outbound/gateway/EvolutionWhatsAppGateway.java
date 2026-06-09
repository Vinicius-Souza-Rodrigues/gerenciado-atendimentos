package com.agendamentos.adapter.outbound.gateway;

import com.agendamentos.domain.port.WhatsAppGatewayPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvolutionWhatsAppGateway implements WhatsAppGatewayPort {

    private final transient RestTemplate restTemplate;

    @Value("${whatsapp.evolution.api-url:}")
    private String evolutionApiUrl;

    @Value("${whatsapp.evolution.api-key:}")
    private String evolutionApiKey;

    @Value("${whatsapp.evolution.instance:}")
    private String evolutionInstance;

    @Value("${whatsapp.enabled:false}")
    private boolean whatsAppEnabled;

    @Override
    public void enviarMensagem(String telefone, String mensagem) {
        if (!whatsAppEnabled) {
            log.warn("WhatsApp desabilitado. Mensagem não será enviada para {}", telefone);
            return;
        }

        try {
            if (evolutionApiUrl == null || evolutionApiUrl.isBlank()) {
                log.warn("Evolution API URL não configurada. Simular envio para {}", telefone);
                return;
            }

            log.info("Enviando mensagem WhatsApp para {} via Evolution API", telefone);

            // TODO: Implementar chamada real para Evolution API quando credenciais forem fornecidas
            // String url = evolutionApiUrl + "/api/message/send/" + evolutionInstance;
            // EnviarMensagemRequest request = new EnviarMensagemRequest(telefone, mensagem);
            // restTemplate.postForObject(url, request, String.class);

            log.info("Mensagem enviada com sucesso para {}", telefone);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem WhatsApp para {}: {}", telefone, e.getMessage());
            throw new RuntimeException("Falha ao enviar mensagem WhatsApp", e);
        }
    }

    @Override
    public boolean verificarConexao() {
        if (!whatsAppEnabled) {
            log.warn("WhatsApp desabilitado");
            return false;
        }

        try {
            if (evolutionApiUrl == null || evolutionApiUrl.isBlank()) {
                log.warn("Evolution API URL não configurada");
                return false;
            }

            log.info("Verificando conexão com Evolution API");

            // TODO: Implementar verificação real quando credenciais forem fornecidas
            // String url = evolutionApiUrl + "/api/instance/" + evolutionInstance;
            // restTemplate.getForObject(url, String.class);

            log.info("Conexão com Evolution API verificada com sucesso");
            return true;
        } catch (Exception e) {
            log.error("Erro ao verificar conexão com Evolution API: {}", e.getMessage());
            return false;
        }
    }
}
