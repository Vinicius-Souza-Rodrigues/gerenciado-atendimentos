package com.agendamentos.domain.valueobject;

import lombok.Value;

@Value
public class NomeServico {
    String valor;

    public NomeServico(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Nome do serviço não pode estar vazio");
        }
        if (valor.length() > 100) {
            throw new IllegalArgumentException("Nome do serviço não pode ter mais de 100 caracteres");
        }
        this.valor = valor.trim();
    }

    @Override
    public String toString() {
        return valor;
    }
}