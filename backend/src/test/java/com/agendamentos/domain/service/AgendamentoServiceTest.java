package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.exception.*;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.port.NotificacaoPort;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AgendamentoService - Testes TDD")
class AgendamentoServiceTest {
    
    @Mock
    private AgendamentoRepositoryPort agendamentoRepository;
    
    @Mock
    private NotificacaoPort notificacaoPort;
    
    private AgendamentoService agendamentoService;
    
    private Cliente cliente;
    private Prestador prestador;
    private LocalDateTime horaFutura;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamentoService = new AgendamentoService(agendamentoRepository, notificacaoPort);
        
        cliente = new Cliente("João Silva", new Telefone("11999999999"));
        prestador = new Prestador("Barbearia XYZ", new Telefone("11988888888"));
        horaFutura = LocalDateTime.now().plusHours(2);
    }
    
    @Test
    @DisplayName("Criar agendamento com horário disponível -> sucesso")
    void givenHorarioDisponivel_whenCriarAgendamento_thenRetornaSucesso() {
        when(agendamentoRepository.listarPorPrestadorEData(prestador.getId(), horaFutura))
            .thenReturn(new ArrayList<>());
        
        Agendamento agendamento = agendamentoService.criar(
            cliente.getId(),
            prestador.getId(),
            horaFutura,
            new NomeServico("Corte de cabelo")
        );
        
        assertNotNull(agendamento);
        assertEquals(cliente.getId(), agendamento.getClienteId());
        assertEquals(prestador.getId(), agendamento.getPrestadorId());
        verify(agendamentoRepository).salvar(any(Agendamento.class));
    }
    
    @Test
    @DisplayName("Criar agendamento com conflito de horário -> exceção")
    void givenHorarioComflito_whenCriarAgendamento_thenLancaExcecao() {
        Agendamento agendamentoExistente = new Agendamento(
            cliente.getId(), prestador.getId(), horaFutura, new NomeServico("Corte")
        ).confirmar();
        
        when(agendamentoRepository.listarPorPrestadorEData(prestador.getId(), horaFutura))
            .thenReturn(List.of(agendamentoExistente));
        
        assertThrows(ConflitoDHorarioException.class, () ->
            agendamentoService.criar(
                cliente.getId(),
                prestador.getId(),
                horaFutura,
                new NomeServico("Corte de cabelo")
            )
        );
    }
    
    @Test
    @DisplayName("Criar agendamento no passado -> exceção")
    void givenHorarioPassado_whenCriarAgendamento_thenLancaExcecao() {
        LocalDateTime horaPassada = LocalDateTime.now().minusHours(1);
        
        assertThrows(AgendamentoNoPassadoException.class, () ->
            agendamentoService.criar(
                cliente.getId(),
                prestador.getId(),
                horaPassada,
                new NomeServico("Corte de cabelo")
            )
        );
    }
}