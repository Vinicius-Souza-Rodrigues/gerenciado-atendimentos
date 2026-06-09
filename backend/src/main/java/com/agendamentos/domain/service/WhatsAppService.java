package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.WhatsAppGatewayPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final WhatsAppGatewayPort whatsAppGateway;

    public void notificarAgendamentoCriado(Agendamento agendamento, Cliente cliente, Prestador prestador) {
        try {
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            var dataFormatada = agendamento.getDataHora().format(formatter);

            var mensagem = "Olá " + cliente.getNome() + "! 👋" +
                    System.lineSeparator() + System.lineSeparator() +
                    "Seu agendamento foi confirmado! ✅" +
                    System.lineSeparator() + System.lineSeparator() +
                    "📅 Data: " + dataFormatada +
                    System.lineSeparator() +
                    "💇 Serviço: " + agendamento.getServico().getValor() +
                    System.lineSeparator() +
                    "🏢 Local: " + prestador.getNomeNegocio() +
                    System.lineSeparator() + System.lineSeparator() +
                    "Qualquer dúvida, entre em contato!";

            whatsAppGateway.enviarMensagem(cliente.getTelefone().getNumero(), mensagem);
            log.info("Notificação de agendamento enviada para {}", cliente.getTelefone().getNumero());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação WhatsApp para agendamento {}: {}", agendamento.getId(), e.getMessage());
        }
    }

    public void notificarStatusAtualizado(Agendamento agendamento, Cliente cliente) {
        try {
            var statusTexto = switch (agendamento.getStatus()) {
                case CONFIRMADO -> "Confirmado ✅";
                case CANCELADO -> "Cancelado ❌";
                case CONCLUIDO -> "Concluído ✔️";
                case PENDENTE -> "Pendente ⏳";
            };

            var mensagem = "Olá " + cliente.getNome() + "!" +
                    System.lineSeparator() + System.lineSeparator() +
                    "Status do seu agendamento foi atualizado:" +
                    System.lineSeparator() + System.lineSeparator() +
                    "📊 Novo Status: " + statusTexto +
                    System.lineSeparator() + System.lineSeparator() +
                    "Obrigado!";

            whatsAppGateway.enviarMensagem(cliente.getTelefone().getNumero(), mensagem);
            log.info("Notificação de atualização de status enviada para {}", cliente.getTelefone().getNumero());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação de status para agendamento {}: {}", agendamento.getId(), e.getMessage());
        }
    }
}
