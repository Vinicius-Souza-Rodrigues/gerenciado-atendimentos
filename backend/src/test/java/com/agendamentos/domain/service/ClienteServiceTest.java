package com.agendamentos.domain.service;

import com.agendamentos.domain.model.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepo;

    private ClienteService service;

    @BeforeEach
    void setUp() {
        service = new ClienteService(clienteRepo);
    }

    @Test
    @DisplayName("deve cadastrar novo cliente quando telefone não existe")
    void deveCadastrarNovoCliente() {
        var telefone = "11999990000";

        when(clienteRepo.buscarPorTelefone(any(Telefone.class))).thenReturn(Optional.empty());
        when(clienteRepo.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        var resultado = service.cadastrar("João Silva", telefone);

        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getTelefone().valor()).isEqualTo(telefone);
        assertThat(resultado.getId()).isNotNull();
        verify(clienteRepo).salvar(any(Cliente.class));
    }

    @Test
    @DisplayName("deve retornar cliente existente quando telefone já cadastrado")
    void deveRetornarClienteExistenteQuandoTelefoneJaCadastrado() {
        var telefone = "11999990000";
        var existente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("João Silva")
                .telefone(new Telefone(telefone))
                .criadoEm(LocalDateTime.now())
                .build();

        when(clienteRepo.buscarPorTelefone(any(Telefone.class))).thenReturn(Optional.of(existente));

        var resultado = service.cadastrar("João Silva", telefone);

        assertThat(resultado).isEqualTo(existente);
        verify(clienteRepo, never()).salvar(any());
    }

    @Test
    @DisplayName("deve buscar cliente por telefone")
    void deveBuscarClientePorTelefone() {
        var telefone = "11999990000";
        var cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Maria")
                .telefone(new Telefone(telefone))
                .criadoEm(LocalDateTime.now())
                .build();

        when(clienteRepo.buscarPorTelefone(any(Telefone.class))).thenReturn(Optional.of(cliente));

        var resultado = service.buscarPorTelefone(telefone);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNome()).isEqualTo("Maria");
    }

    @Test
    @DisplayName("deve retornar vazio quando cliente não encontrado")
    void deveRetornarVazioQuandoClienteNaoEncontrado() {
        when(clienteRepo.buscarPorTelefone(any(Telefone.class))).thenReturn(Optional.empty());

        var resultado = service.buscarPorTelefone("11999990000");

        assertThat(resultado).isEmpty();
        verify(clienteRepo, never()).salvar(any());
    }

    @Test
    @DisplayName("deve rejeitar telefone inválido")
    void deveRejeitarTelefoneInvalido() {
        assertThatThrownBy(() -> service.cadastrar("João", "123"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
