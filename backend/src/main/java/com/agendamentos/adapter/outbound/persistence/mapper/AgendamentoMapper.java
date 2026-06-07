package com.agendamentos.adapter.outbound.persistence.mapper;

import com.agendamentos.adapter.outbound.persistence.entity.AgendamentoEntity;
import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;

public class AgendamentoMapper {

    public static Agendamento toDomain(AgendamentoEntity entity) {
        return Agendamento.comId(
                entity.getId(),
                entity.getClienteId(),
                entity.getPrestadorId(),
                entity.getDataHora(),
                new NomeServico(entity.getServico()),
                StatusAgendamento.valueOf(entity.getStatus().name()),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
    }

    public static AgendamentoEntity toEntity(Agendamento agendamento) {
        var entity = new AgendamentoEntity();
        entity.setId(agendamento.getId());
        entity.setClienteId(agendamento.getClienteId());
        entity.setPrestadorId(agendamento.getPrestadorId());
        entity.setDataHora(agendamento.getDataHora());
        entity.setServico(agendamento.getServico().getValor());
        entity.setStatus(AgendamentoEntity.StatusAgendamento.valueOf(agendamento.getStatus().name()));
        entity.setCriadoEm(agendamento.getCriadoEm());
        entity.setAtualizadoEm(agendamento.getAtualizadoEm());
        return entity;
    }

}
