package com.agendamentos.adapter.outbound.persistence.mapper;

import com.agendamentos.adapter.outbound.persistence.entity.HorarioDisponivelEntity;
import com.agendamentos.domain.entity.HorarioDisponivel;
import org.springframework.stereotype.Component;

@Component
public class HorarioDisponivelMapper {

    public HorarioDisponivel toDomain(HorarioDisponivelEntity entity) {
        if (entity == null) {
            return null;
        }
        return HorarioDisponivel.comId(
            entity.getId(),
            entity.getPrestadorId(),
            entity.getDiaDaSemana(),
            entity.getHoraInicio(),
            entity.getHoraFim(),
            entity.isAtivo()
        );
    }

    public HorarioDisponivelEntity toEntity(HorarioDisponivel domain) {
        if (domain == null) {
            return null;
        }
        return HorarioDisponivelEntity.builder()
            .id(domain.getId())
            .prestadorId(domain.getPrestadorId())
            .diaDaSemana(domain.getDiaDaSemana())
            .horaInicio(domain.getHoraInicio())
            .horaFim(domain.getHoraFim())
            .ativo(domain.isAtivo())
            .build();
    }
}
