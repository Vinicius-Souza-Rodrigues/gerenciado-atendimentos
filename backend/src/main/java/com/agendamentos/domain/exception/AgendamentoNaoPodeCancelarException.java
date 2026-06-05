package com.agendamentos.domain.exception;

public class AgendamentoNaoPodeCancelarException extends RuntimeException {
    public AgendamentoNaoPodeCancelarException(String mensagem) {
        super(mensagem);
    }
}