package com.agendamentos.adapter.outbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoEntity, UUID> {

    @Query("SELECT a FROM AgendamentoEntity a " +
           "WHERE a.prestadorId = :prestadorId " +
           "AND a.dataHora BETWEEN :inicio AND :fim " +
           "AND a.status = 'CONFIRMADO'")
    List<AgendamentoEntity> findConfirmadosByPrestadorAndPeriodo(
            @Param("prestadorId") UUID prestadorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT a FROM AgendamentoEntity a " +
           "WHERE a.clienteId = :clienteId " +
           "ORDER BY a.dataHora DESC")
    List<AgendamentoEntity> findByClienteIdOrderByDataHoraDesc(@Param("clienteId") UUID clienteId);

}
