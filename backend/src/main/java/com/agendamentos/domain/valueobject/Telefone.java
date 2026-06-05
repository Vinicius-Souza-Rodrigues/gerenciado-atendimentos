package com.agendamentos.domain.valueobject;

import lombok.Value;

@Value
public class Telefone {
    String numero;

    public Telefone(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode estar vazio");
        }
        String limpo = numero.replaceAll("[^0-9]", "");
        if (limpo.length() < 10 || limpo.length() > 15) {
            throw new IllegalArgumentException("Telefone inválido: deve ter entre 10 e 15 dígitos");
        }
        this.numero = limpo;
    }

    @Override
    public String toString() {
        return numero;
    }
}