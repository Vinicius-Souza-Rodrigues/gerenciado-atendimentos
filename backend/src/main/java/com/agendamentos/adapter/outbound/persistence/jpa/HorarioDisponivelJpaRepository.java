package com.agendamentos.adapter.outbound.persistence.jpa;

import com.agendamentos.adapter.outbound.persistence.entity.HorarioDisponivelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface HorarioDisponivelJpaRepository extends JpaRepository<HorarioDisponivelEntity, UUID> {
    List<HorarioDisponivelEntity> findByPrestadorId(UUID prestadorId);
    List<HorarioDisponivelEntity> findByPrestadorIdAndDiaDaSemana(UUID prestadorId, DayOfWeek dia);
    void deleteByPrestadorId(UUID prestadorId);
}
