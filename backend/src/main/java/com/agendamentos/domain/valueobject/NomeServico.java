package com.agendamentos.domain.valueobject;

public record NomeServico(String valor) {

    public NomeServico {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Nome do serviço não pode ser vazio");
        }
        valor = valor.trim();
        if (valor.length() > 100) {
            throw new IllegalArgumentException("Nome do serviço excede 100 caracteres");
        }
    }

}
