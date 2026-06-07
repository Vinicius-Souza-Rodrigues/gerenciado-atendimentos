package com.agendamentos.adapter.outbound.persistence.repository;

import com.agendamentos.adapter.outbound.persistence.jpa.PrestadorJpaRepository;
import com.agendamentos.adapter.outbound.persistence.mapper.PrestadorMapper;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PrestadorRepositoryAdapter implements PrestadorRepositoryPort {

    private final PrestadorJpaRepository jpaRepository;

    @Override
    public Prestador salvar(Prestador prestador) {
        var entity = PrestadorMapper.toEntity(prestador);
        var saved = jpaRepository.save(entity);
        return PrestadorMapper.toDomain(saved);
    }

    @Override
    public Optional<Prestador> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(PrestadorMapper::toDomain);
    }

    @Override
    public List<Prestador> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(PrestadorMapper::toDomain)
                .toList();
    }
}
