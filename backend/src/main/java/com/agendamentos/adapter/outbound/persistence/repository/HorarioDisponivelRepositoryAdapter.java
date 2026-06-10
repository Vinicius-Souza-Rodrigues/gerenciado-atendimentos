package com.agendamentos.adapter.outbound.persistence.repository;

import com.agendamentos.adapter.outbound.persistence.jpa.HorarioDisponivelJpaRepository;
import com.agendamentos.adapter.outbound.persistence.mapper.HorarioDisponivelMapper;
import com.agendamentos.domain.entity.HorarioDisponivel;
import com.agendamentos.domain.port.HorarioDisponivelRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HorarioDisponivelRepositoryAdapter implements HorarioDisponivelRepositoryPort {

    private final HorarioDisponivelJpaRepository jpaRepository;
    private final HorarioDisponivelMapper mapper;

    @Override
    public HorarioDisponivel salvar(HorarioDisponivel horario) {
        var entity = mapper.toEntity(horario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<HorarioDisponivel> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<HorarioDisponivel> listarPorPrestador(UUID prestadorId) {
        return jpaRepository.findByPrestadorId(prestadorId)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<HorarioDisponivel> listarPorPrestadorEDia(UUID prestadorId, DayOfWeek dia) {
        return jpaRepository.findByPrestadorIdAndDiaDaSemana(prestadorId, dia)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void deletarPorId(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deletarTodosPorPrestador(UUID prestadorId) {
        jpaRepository.deleteByPrestadorId(prestadorId);
    }
}
