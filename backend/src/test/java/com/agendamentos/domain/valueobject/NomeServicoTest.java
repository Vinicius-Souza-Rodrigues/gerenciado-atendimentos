package com.agendamentos.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NomeServico - Value Object")
class NomeServicoTest {
    
    @Test
    @DisplayName("Criar serviço válido -> sucesso")
    void givenNomeServicoValido_whenCriar_thenSucesso() {
        NomeServico servico = new NomeServico("Corte de cabelo");
        assertEquals("Corte de cabelo", servico.getValor());
    }
    
    @Test
    @DisplayName("Nome de serviço com espaços -> trim")
    void givenNomeComEspacos_whenCriar_thenTrim() {
        NomeServico servico = new NomeServico("  Corte  ");
        assertEquals("Corte", servico.getValor());
    }
    
    @Test
    @DisplayName("Nome de serviço vazio -> exceção")
    void givenNomeVazio_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new NomeServico(""));
    }
    
    @Test
    @DisplayName("Nome de serviço nulo -> exceção")
    void givenNomeNulo_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new NomeServico(null));
    }
    
    @Test
    @DisplayName("Nome muito longo -> exceção")
    void givenNomeMuitoLongo_whenCriar_thenExcecao() {
        String nomeLongo = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new NomeServico(nomeLongo));
    }
    
    @Test
    @DisplayName("Nome com exatamente 100 chars -> sucesso")
    void givenNomeComExatamente100Chars_whenCriar_thenSucesso() {
        String nome100 = "a".repeat(100);
        NomeServico servico = new NomeServico(nome100);
        assertEquals(100, servico.getValor().length());
    }
}