package com.agendamentos.adapter.outbound.gateway;

import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.port.NotificacaoPort;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificacaoAdapter implements NotificacaoPort {

    @Override
    public void enviarConfirmacao(Telefone telefone, Agendamento agendamento) {
        log.info("Notificação de confirmação enviada para: {}", telefone.getNumero());
    }

    @Override
    public void enviarRejeicao(Telefone telefone, String motivo) {
        log.info("Notificação de rejeição enviada para: {} - Motivo: {}", telefone.getNumero(), motivo);
    }

}
