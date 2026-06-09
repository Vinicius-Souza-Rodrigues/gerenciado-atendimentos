package com.agendamentos.adapter.outbound.persistence.mapper;

import com.agendamentos.adapter.outbound.persistence.entity.ClienteEntity;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.valueobject.Telefone;

public class ClienteMapper {

    public static Cliente toDomain(ClienteEntity entity) {
        return Cliente.comId(
                entity.getId(),
                entity.getNome(),
                new Telefone(entity.getTelefone()),
                entity.getTelegramChatId(),
                entity.getCriadoEm()
        );
    }

    public static ClienteEntity toEntity(Cliente cliente) {
        var entity = new ClienteEntity();
        entity.setId(cliente.getId());
        entity.setNome(cliente.getNome());
        entity.setTelefone(cliente.getTelefone().getNumero());
        entity.setTelegramChatId(cliente.getTelegramChatId());
        entity.setCriadoEm(cliente.getCriadoEm());
        return entity;
    }

}
