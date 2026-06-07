package com.agendamentos.adapter.outbound.persistence.mapper;

import com.agendamentos.adapter.outbound.persistence.entity.PrestadorEntity;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.valueobject.Telefone;

public class PrestadorMapper {

    public static Prestador toDomain(PrestadorEntity entity) {
        return Prestador.comId(
                entity.getId(),
                entity.getNomeNegocio(),
                new Telefone(entity.getTelefoneWhatsApp()),
                entity.getCriadoEm()
        );
    }

    public static PrestadorEntity toEntity(Prestador prestador) {
        var entity = new PrestadorEntity();
        entity.setId(prestador.getId());
        entity.setNomeNegocio(prestador.getNomeNegocio());
        entity.setTelefoneWhatsApp(prestador.getTelefoneWhatsApp().getNumero());
        entity.setCriadoEm(prestador.getCriadoEm());
        return entity;
    }

}
