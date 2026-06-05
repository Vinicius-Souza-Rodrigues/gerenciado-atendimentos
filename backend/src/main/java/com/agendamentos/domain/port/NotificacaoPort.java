package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.valueobject.Telefone;

public interface NotificacaoPort {
    void enviarConfirmacao(Telefone telefone, Agendamento agendamento);
    void enviarRejeicao(Telefone telefone, String motivo);
}