package com.agendamentos.domain.exception;

public class AgendamentoNoPassadoException extends RuntimeException {
    public AgendamentoNoPassadoException(String mensagem) {
        super(mensagem);
    }
}