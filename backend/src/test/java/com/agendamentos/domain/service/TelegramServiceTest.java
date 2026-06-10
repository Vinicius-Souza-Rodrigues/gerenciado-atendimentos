package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.TelegramGatewayPort;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("TelegramService - Testes de notificações")
class TelegramServiceTest {

    private static final String TELEGRAM_CHAT_ID = "123456789";

    @Mock
    private TelegramGatewayPort telegramGateway;

    private TelegramService telegramService;

    private Cliente clienteComTelegram;
    private Cliente clienteSemTelegram;
    private Prestador prestador;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telegramService = new TelegramService(telegramGateway);

        clienteComTelegram = new Cliente("João Silva", new Telefone("11999999999"), TELEGRAM_CHAT_ID);
        clienteSemTelegram = new Cliente("Maria Santos", new Telefone("11988888888"));
        prestador = new Prestador("Barbearia XYZ", new Telefone("11977777777"));
        agendamento = new Agendamento(
                clienteComTelegram.getId(),
                prestador.getId(),
                LocalDateTime.now().plusHours(2),
                new NomeServico("Corte de cabelo")
        ).confirmar();
    }

    @Test
    @DisplayName("Notificar agendamento criado com telegramChatId -> envia mensagem")
    void givenClienteComTelegram_whenNotificarAgendamentoCriado_thenEnviaMensagem() {
        telegramService.notificarAgendamentoCriado(agendamento, clienteComTelegram, prestador);

        verify(telegramGateway).enviarMensagem(eq(TELEGRAM_CHAT_ID), anyString());
    }

    @Test
    @DisplayName("Notificar agendamento criado sem telegramChatId -> ignora")
    void givenClienteSemTelegram_whenNotificarAgendamentoCriado_thenIgnora() {
        telegramService.notificarAgendamentoCriado(agendamento, clienteSemTelegram, prestador);

        verify(telegramGateway, never()).enviarMensagem(anyString(), anyString());
    }

    @Test
    @DisplayName("Notificar status atualizado com telegramChatId -> envia mensagem")
    void givenClienteComTelegram_whenNotificarStatusAtualizado_thenEnviaMensagem() {
        telegramService.notificarStatusAtualizado(agendamento, clienteComTelegram);

        verify(telegramGateway).enviarMensagem(eq(TELEGRAM_CHAT_ID), anyString());
    }

    @Test
    @DisplayName("Mensagem deve conter nome do cliente")
    void givenAgendamento_whenNotificar_thenMensagemContemNomeCliente() {
        telegramService.notificarAgendamentoCriado(agendamento, clienteComTelegram, prestador);

        verify(telegramGateway).enviarMensagem(
                eq(TELEGRAM_CHAT_ID),
                argThat(msg -> msg.contains(clienteComTelegram.getNome()))
        );
    }
}
