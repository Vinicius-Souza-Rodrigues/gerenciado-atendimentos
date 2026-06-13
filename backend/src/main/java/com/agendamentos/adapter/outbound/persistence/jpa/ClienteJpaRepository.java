package com.agendamentos.adapter.outbound.persistence.jpa;

import com.agendamentos.adapter.outbound.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    Optional<ClienteEntity> findByTelefone(String telefone);

    Optional<ClienteEntity> findByTelegramChatId(String telegramChatId);

    @Query("SELECT DISTINCT c FROM ClienteEntity c WHERE c.id IN "
            + "(SELECT a.clienteId FROM AgendamentoEntity a WHERE a.prestadorId = :prestadorId)")
    List<ClienteEntity> findByPrestadorId(@Param("prestadorId") UUID prestadorId);

}
