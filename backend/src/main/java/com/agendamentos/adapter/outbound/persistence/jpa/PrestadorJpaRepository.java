package com.agendamentos.adapter.outbound.persistence.jpa;

import com.agendamentos.adapter.outbound.persistence.entity.PrestadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrestadorJpaRepository extends JpaRepository<PrestadorEntity, UUID> {

}
