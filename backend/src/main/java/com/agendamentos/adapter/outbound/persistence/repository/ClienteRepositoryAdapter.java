package com.agendamentos.adapter.outbound.persistence.repository;

import com.agendamentos.adapter.outbound.persistence.jpa.ClienteJpaRepository;
import com.agendamentos.adapter.outbound.persistence.mapper.ClienteMapper;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;

    @Override
    public Cliente salvar(Cliente cliente) {
        var entity = ClienteMapper.toEntity(cliente);
        var saved = jpaRepository.save(entity);
        return ClienteMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorTelefone(Telefone telefone) {
        return jpaRepository.findByTelefone(telefone.getNumero())
                .map(ClienteMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorTelegramChatId(String chatId) {
        return jpaRepository.findByTelegramChatId(chatId)
                .map(ClienteMapper::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(ClienteMapper::toDomain)
                .toList();
    }

}
