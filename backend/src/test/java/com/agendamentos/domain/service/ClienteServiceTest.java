package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ClienteService - Testes TDD")
class ClienteServiceTest {
    
    @Mock
    private ClienteRepositoryPort clienteRepository;
    
    private ClienteService clienteService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clienteService = new ClienteService(clienteRepository);
    }
    
    @Test
    @DisplayName("Cadastrar novo cliente -> sucesso")
    void givenNovoCliente_whenCadastrar_thenSalva() {
        Telefone telefone = new Telefone("11999999999");
        String nome = "João Silva";
        
        Cliente clienteCapturado = new Cliente(nome, telefone);
        when(clienteRepository.salvar(any(Cliente.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        Cliente cliente = clienteService.cadastrar(nome, telefone);
        
        assertNotNull(cliente);
        assertEquals(nome, cliente.getNome());
        assertEquals(telefone, cliente.getTelefone());
        assertNotNull(cliente.getId());
        verify(clienteRepository).salvar(any(Cliente.class));
    }
    
    @Test
    @DisplayName("Buscar cliente por telefone -> encontra")
    void givenTelefoneExistente_whenBuscar_thenRetornaCliente() {
        Telefone telefone = new Telefone("11999999999");
        Cliente clienteEsperado = new Cliente("Maria Silva", telefone);
        when(clienteRepository.buscarPorTelefone(telefone))
            .thenReturn(Optional.of(clienteEsperado));
        
        Optional<Cliente> resultado = clienteService.buscarPorTelefone(telefone);
        
        assertTrue(resultado.isPresent());
        assertEquals(clienteEsperado.getNome(), resultado.get().getNome());
        verify(clienteRepository).buscarPorTelefone(telefone);
    }
    
    @Test
    @DisplayName("Buscar cliente por telefone inexistente -> não encontra")
    void givenTelefoneInexistente_whenBuscar_thenNaoRetorna() {
        Telefone telefone = new Telefone("11999999999");
        when(clienteRepository.buscarPorTelefone(telefone))
            .thenReturn(Optional.empty());
        
        Optional<Cliente> resultado = clienteService.buscarPorTelefone(telefone);
        
        assertTrue(resultado.isEmpty());
        verify(clienteRepository).buscarPorTelefone(telefone);
    }
}