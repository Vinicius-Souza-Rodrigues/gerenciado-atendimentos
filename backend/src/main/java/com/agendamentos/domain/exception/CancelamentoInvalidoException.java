package com.agendamentos.domain.exception;

public class CancelamentoInvalidoException extends RuntimeException {
    public CancelamentoInvalidoException(String message) {
        super(message);
    }
}
