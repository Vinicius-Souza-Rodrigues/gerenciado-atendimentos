package com.agendamentos.adapter.outbound.persistence.jpa;

import com.agendamentos.adapter.outbound.persistence.entity.TelegramContextoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramContextoJpaRepository extends JpaRepository<TelegramContextoEntity, String> {}
