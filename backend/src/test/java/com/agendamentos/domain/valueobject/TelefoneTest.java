package com.agendamentos.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Telefone - Value Object")
class TelefoneTest {
    
    @Test
    @DisplayName("Criar telefone válido -> sucesso")
    void givenTelefoneValido_whenCriar_thenSucesso() {
        Telefone telefone = new Telefone("11999999999");
        assertNotNull(telefone);
        assertEquals("11999999999", telefone.getNumero());
    }
    
    @Test
    @DisplayName("Criar telefone com formatação -> normaliza")
    void givenTelefoneComFormatacao_whenCriar_thenNormaliza() {
        Telefone telefone = new Telefone("(11) 99999-9999");
        assertEquals("11999999999", telefone.getNumero());
    }
    
    @Test
    @DisplayName("Telefone vazio -> exceção")
    void givenTelefoneVazio_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Telefone(""));
    }
    
    @Test
    @DisplayName("Telefone nulo -> exceção")
    void givenTelefoneNulo_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Telefone(null));
    }
    
    @Test
    @DisplayName("Telefone com poucos dígitos -> exceção")
    void givenTelefoneComPoucosDígitos_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Telefone("123456"));
    }
    
    @Test
    @DisplayName("Telefone com muitos dígitos -> exceção")
    void givenTelefoneComMuitosDígitos_whenCriar_thenExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Telefone("123456789012345678"));
    }
}