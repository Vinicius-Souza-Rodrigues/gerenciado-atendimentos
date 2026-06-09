package com.agendamentos.domain.entity;

import com.agendamentos.domain.valueobject.Telefone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cliente - Entidade")
class ClienteTest {
    
    @Test
    @DisplayName("Criar cliente válido -> sucesso")
    void givenClienteValido_whenCriar_thenSucesso() {
        Cliente cliente = new Cliente("João Silva", new Telefone("11999999999"));
        
        assertNotNull(cliente);
        assertNotNull(cliente.getId());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("11999999999", cliente.getTelefone().getNumero());
        assertNotNull(cliente.getCriadoEm());
    }
    
    @Test
    @DisplayName("Cliente sem nome -> exceção")
    void givenClienteSemNome_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Cliente("", new Telefone("11999999999"))
        );
    }
    
    @Test
    @DisplayName("Cliente sem telefone -> exceção")
    void givenClienteSemTelefone_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Cliente("João", null)
        );
    }
    
    @Test
    @DisplayName("Restaurar cliente por ID -> funciona")
    void givenClienteComId_whenRestauraComId_thenSucesso() {
        Cliente cliente = new Cliente("Maria", new Telefone("11988888888"));
        Cliente restaurado = Cliente.comId(cliente.getId(), "Maria", new Telefone("11988888888"), null, cliente.getCriadoEm());
        
        assertEquals(cliente.getId(), restaurado.getId());
        assertEquals(cliente.getNome(), restaurado.getNome());
    }
}