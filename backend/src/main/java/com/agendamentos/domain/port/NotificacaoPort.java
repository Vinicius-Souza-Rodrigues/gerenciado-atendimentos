package com.agendamentos.domain.port;

import com.agendamentos.domain.model.Agendamento;
import com.agendamentos.domain.valueobject.Telefone;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacaoPort {

    void enviarConfirmacao(Telefone telefone, Agendamento agendamento);

    void enviarRejeicao(Telefone telefone, String motivo);

    void enviarConsultaHorarios(Telefone telefone, List<LocalDateTime> slots);

}
