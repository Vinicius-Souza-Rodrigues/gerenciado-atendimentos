package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.TelegramGatewayPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramGatewayPort telegramGateway;

    public void enviarMensagem(String chatId, String mensagem) {
        try {
            telegramGateway.enviarMensagem(chatId, mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem Telegram para chat {}: {}", chatId, e.getMessage());
        }
    }

    public void notificarAgendamentoCriado(Agendamento agendamento, Cliente cliente, Prestador prestador) {
        if (cliente.getTelegramChatId() == null) {
            log.debug("Cliente {} sem telegramChatId, notificação Telegram ignorada", cliente.getId());
            return;
        }
        try {
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            var dataFormatada = agendamento.getDataHora().format(formatter);

            var mensagem = "<b>Olá " + cliente.getNome() + "! 👋</b>\n\n" +
                    "Seu agendamento foi confirmado! ✅\n\n" +
                    "<b>📅 Data:</b> " + dataFormatada + "\n" +
                    "<b>💇 Serviço:</b> " + agendamento.getServico().getValor() + "\n" +
                    "<b>🏢 Local:</b> " + prestador.getNomeNegocio() + "\n\n" +
                    "Qualquer dúvida, entre em contato!";

            telegramGateway.enviarMensagem(cliente.getTelegramChatId(), mensagem);
            log.info("Notificação de agendamento enviada via Telegram para chat {}", cliente.getTelegramChatId());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação Telegram para agendamento {}: {}", agendamento.getId(), e.getMessage());
        }
    }

    public void notificarStatusAtualizado(Agendamento agendamento, Cliente cliente) {
        if (cliente.getTelegramChatId() == null) {
            log.debug("Cliente {} sem telegramChatId, notificação Telegram ignorada", cliente.getId());
            return;
        }
        try {
            var statusTexto = switch (agendamento.getStatus()) {
                case CONFIRMADO -> "Confirmado ✅";
                case CANCELADO -> "Cancelado ❌";
                case CONCLUIDO -> "Concluído ✔️";
                case PENDENTE -> "Pendente ⏳";
            };

            var mensagem = "<b>Olá " + cliente.getNome() + "!</b>\n\n" +
                    "Status do seu agendamento foi atualizado:\n\n" +
                    "<b>📊 Novo Status:</b> " + statusTexto + "\n\n" +
                    "Obrigado!";

            telegramGateway.enviarMensagem(cliente.getTelegramChatId(), mensagem);
            log.info("Notificação de status enviada via Telegram para chat {}", cliente.getTelegramChatId());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação de status Telegram para agendamento {}: {}", agendamento.getId(), e.getMessage());
        }
    }
}
