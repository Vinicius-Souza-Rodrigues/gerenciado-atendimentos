package com.agendamentos.domain.service;

import com.agendamentos.domain.exception.AgendamentoPassadoException;
import com.agendamentos.domain.exception.CancelamentoInvalidoException;
import com.agendamentos.domain.exception.ConflitoDHorarioException;
import com.agendamentos.domain.model.Agendamento;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepositoryPort agendamentoRepo;

    private AgendamentoService service;

    private static final UUID CLIENTE_ID = UUID.randomUUID();
    private static final UUID PRESTADOR_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AgendamentoService(agendamentoRepo);
    }

    @Test
    @DisplayName("deve criar agendamento quando horário está disponível")
    void deveCriarAgendamentoQuandoHorarioDisponivel() {
        var dataHora = LocalDateTime.now().plusDays(1);

        when(agendamentoRepo.listarConfirmadosPorPrestadorEPeriodo(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(agendamentoRepo.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.criar(CLIENTE_ID, PRESTADOR_ID, dataHora, "Corte de Cabelo");

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.PENDENTE);
        assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID);
        assertThat(resultado.getPrestadorId()).isEqualTo(PRESTADOR_ID);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora);
        verify(agendamentoRepo).salvar(any(Agendamento.class));
    }

    @Test
    @DisplayName("deve lançar exceção quando já existe agendamento confirmado no mesmo horário")
    void deveLancarExcecaoQuandoConflitoDHorario() {
        var dataHora = LocalDateTime.now().plusDays(1);
        var existente = agendamentoConfirmado(PRESTADOR_ID, dataHora);

        when(agendamentoRepo.listarConfirmadosPorPrestadorEPeriodo(any(), any(), any()))
                .thenReturn(List.of(existente));

        assertThatThrownBy(() -> service.criar(CLIENTE_ID, PRESTADOR_ID, dataHora, "Corte"))
                .isInstanceOf(ConflitoDHorarioException.class);

        verify(agendamentoRepo, never()).salvar(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando horário é no passado")
    void deveLancarExcecaoQuandoHorarioNoPassado() {
        var dataHoraPassado = LocalDateTime.now().minusHours(1);

        assertThatThrownBy(() -> service.criar(CLIENTE_ID, PRESTADOR_ID, dataHoraPassado, "Corte"))
                .isInstanceOf(AgendamentoPassadoException.class);

        verify(agendamentoRepo, never()).listarConfirmadosPorPrestadorEPeriodo(any(), any(), any());
        verify(agendamentoRepo, never()).salvar(any());
    }

    @Test
    @DisplayName("deve cancelar agendamento confirmado com sucesso")
    void deveCancelarAgendamentoConfirmado() {
        var id = UUID.randomUUID();
        var agendamento = agendamentoConfirmado(PRESTADOR_ID, LocalDateTime.now().plusDays(1));

        when(agendamentoRepo.buscarPorId(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepo.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.cancelar(id);

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
        verify(agendamentoRepo).salvar(any(Agendamento.class));
    }

    @Test
    @DisplayName("deve lançar exceção ao tentar cancelar agendamento concluído")
    void deveLancarExcecaoAoCancelarAgendamentoConcluido() {
        var id = UUID.randomUUID();
        var agendamento = Agendamento.builder()
                .id(id)
                .clienteId(CLIENTE_ID)
                .prestadorId(PRESTADOR_ID)
                .dataHora(LocalDateTime.now().plusDays(1))
                .servico(new NomeServico("Corte"))
                .status(StatusAgendamento.CONCLUIDO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        when(agendamentoRepo.buscarPorId(id)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> service.cancelar(id))
                .isInstanceOf(CancelamentoInvalidoException.class);

        verify(agendamentoRepo, never()).salvar(any());
    }

    private Agendamento agendamentoConfirmado(UUID prestadorId, LocalDateTime dataHora) {
        return Agendamento.builder()
                .id(UUID.randomUUID())
                .clienteId(UUID.randomUUID())
                .prestadorId(prestadorId)
                .dataHora(dataHora)
                .servico(new NomeServico("Corte"))
                .status(StatusAgendamento.CONFIRMADO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }
}
