package com.agendamentos.adapter.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "telegram_contexto")
@Data
public class TelegramContextoEntity {

    @Id
    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "prestador_id", nullable = false)
    private UUID prestadorId;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
