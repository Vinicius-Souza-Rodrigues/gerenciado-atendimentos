package com.agendamentos.domain.exception;

public class HorarioIndisponivelException extends RuntimeException {
    public HorarioIndisponivelException(String mensagem) {
        super(mensagem);
    }
}