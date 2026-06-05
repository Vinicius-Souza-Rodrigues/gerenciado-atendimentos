package com.agendamentos.domain.entity;

import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;
import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Agendamento - Entidade")
class AgendamentoTest {
    
    private UUID clienteId;
    private UUID prestadorId;
    private LocalDateTime horaFutura;
    private NomeServico servico;
    
    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        prestadorId = UUID.randomUUID();
        horaFutura = LocalDateTime.now().plusHours(2);
        servico = new NomeServico("Corte");
    }
    
    @Test
    @DisplayName("Criar agendamento -> status PENDENTE")
    void givenNovoAgendamento_whenCriar_thenStatusPendente() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico);
        
        assertNotNull(agendamento.getId());
        assertEquals(StatusAgendamento.PENDENTE, agendamento.getStatus());
        assertEquals(clienteId, agendamento.getClienteId());
    }
    
    @Test
    @DisplayName("Confirmar agendamento -> muda status")
    void givenAgendamentoPendente_whenConfirmar_thenStatusConfirmado() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico);
        Agendamento confirmado = agendamento.confirmar();
        
        assertEquals(StatusAgendamento.CONFIRMADO, confirmado.getStatus());
        assertEquals(agendamento.getId(), confirmado.getId());
    }
    
    @Test
    @DisplayName("Cancelar agendamento confirmado -> status CANCELADO")
    void givenAgendamentoConfirmado_whenCancelar_thenStatusCancelado() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico).confirmar();
        Agendamento cancelado = agendamento.cancelar();
        
        assertEquals(StatusAgendamento.CANCELADO, cancelado.getStatus());
    }
    
    @Test
    @DisplayName("Cancelar agendamento concluído -> exceção")
    void givenAgendamentoConcluido_whenCancelar_thenExcecao() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico)
            .confirmar().concluir();
        
        assertThrows(IllegalStateException.class, agendamento::cancelar);
    }
    
    @Test
    @DisplayName("Concluir agendamento -> status CONCLUIDO")
    void givenAgendamento_whenConcluir_thenStatusConcluido() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico).confirmar();
        Agendamento concluido = agendamento.concluir();
        
        assertEquals(StatusAgendamento.CONCLUIDO, concluido.getStatus());
    }
    
    @Test
    @DisplayName("Verificar se está confirmado -> true")
    void givenAgendamentoConfirmado_whenEstahConfirmado_thenTrue() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico).confirmar();
        
        assertTrue(agendamento.estahConfirmado());
    }
    
    @Test
    @DisplayName("Verificar se está confirmado (pendente) -> false")
    void givenAgendamentoPendente_whenEstahConfirmado_thenFalse() {
        Agendamento agendamento = new Agendamento(clienteId, prestadorId, horaFutura, servico);
        
        assertFalse(agendamento.estahConfirmado());
    }
}