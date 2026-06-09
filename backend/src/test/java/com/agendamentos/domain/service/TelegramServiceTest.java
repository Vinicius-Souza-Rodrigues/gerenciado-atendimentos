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
import static org.mockito.Mockito.verify;

@DisplayName("TelegramService - Testes de notificações")
class TelegramServiceTest {

    @Mock
    private TelegramGatewayPort telegramGateway;

    private TelegramService telegramService;

    private Cliente cliente;
    private Prestador prestador;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        telegramService = new TelegramService(telegramGateway);

        cliente = new Cliente("João Silva", new Telefone("11999999999"));
        prestador = new Prestador("Barbearia XYZ", new Telefone("11988888888"));
        agendamento = new Agendamento(
                cliente.getId(),
                prestador.getId(),
                LocalDateTime.now().plusHours(2),
                new NomeServico("Corte de cabelo")
        ).confirmar();
    }

    @Test
    @DisplayName("Notificar agendamento criado -> deve enviar mensagem")
    void givenAgendamento_whenNotificarAgendamentoCriado_thenEnviaMensagem() {
        telegramService.notificarAgendamentoCriado(agendamento, cliente, prestador);

        verify(telegramGateway).enviarMensagem(
                cliente.getTelefone().getNumero(),
                anyString()
        );
    }

    @Test
    @DisplayName("Notificar status atualizado -> deve enviar mensagem")
    void givenAgendamento_whenNotificarStatusAtualizado_thenEnviaMensagem() {
        agendamento.confirmar();

        telegramService.notificarStatusAtualizado(agendamento, cliente);

        verify(telegramGateway).enviarMensagem(
                cliente.getTelefone().getNumero(),
                anyString()
        );
    }

    @Test
    @DisplayName("Mensagem deve conter nome do cliente")
    void givenAgendamento_whenNotificar_thenMensagemContemNomeCliente() {
        telegramService.notificarAgendamentoCriado(agendamento, cliente, prestador);

        verify(telegramGateway).enviarMensagem(
                eq(cliente.getTelefone().getNumero()),
                argThat(msg -> msg.contains(cliente.getNome()))
        );
    }
}
