package com.agendamentos.domain.valueobject;

public record Telefone(String valor) {

    public Telefone {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        valor = valor.replaceAll("[^0-9]", "");
        if (valor.length() < 10 || valor.length() > 13) {
            throw new IllegalArgumentException("Telefone inválido: deve ter entre 10 e 13 dígitos");
        }
    }

}
