package com.agendamentos.adapter.outbound.persistence.repository;

import com.agendamentos.adapter.outbound.persistence.jpa.AgendamentoJpaRepository;
import com.agendamentos.adapter.outbound.persistence.mapper.AgendamentoMapper;
import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgendamentoRepositoryAdapter implements AgendamentoRepositoryPort {

    private final AgendamentoJpaRepository jpaRepository;

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        var entity = AgendamentoMapper.toEntity(agendamento);
        var saved = jpaRepository.save(entity);
        return AgendamentoMapper.toDomain(saved);
    }

    @Override
    public Optional<Agendamento> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(AgendamentoMapper::toDomain);
    }

    @Override
    public List<Agendamento> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(AgendamentoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Agendamento> listarPorPrestador(UUID prestadorId) {
        return jpaRepository.findAll().stream()
                .filter(a -> a.getPrestadorId().equals(prestadorId))
                .map(AgendamentoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Agendamento> listarPorPrestadorEData(UUID prestadorId, LocalDateTime data) {
        var fim = data.plusHours(1);
        return jpaRepository.findConfirmadosByPrestadorAndPeriodo(prestadorId, data, fim).stream()
                .map(AgendamentoMapper::toDomain)
                .toList();
    }

    @Override
    public List<Agendamento> listarPorCliente(UUID clienteId) {
        return jpaRepository.findByClienteIdOrderByDataHoraDesc(clienteId).stream()
                .map(AgendamentoMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflito(UUID prestadorId, LocalDateTime inicio, LocalDateTime fim) {
        return !jpaRepository.findConfirmadosByPrestadorAndPeriodo(prestadorId, inicio, fim).isEmpty();
    }

}
