package com.agendamentos.domain.exception;

public class ConflitoDHorarioException extends RuntimeException {
    public ConflitoDHorarioException(String mensagem) {
        super(mensagem);
    }
}