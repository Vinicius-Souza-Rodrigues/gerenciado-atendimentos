package com.agendamentos.domain.entity;

import com.agendamentos.domain.valueobject.Telefone;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Cliente {
    UUID id;
    String nome;
    Telefone telefone;
    String telegramChatId;
    LocalDateTime criadoEm;

    public Cliente(String nome, Telefone telefone) {
        this(nome, telefone, null);
    }

    public Cliente(String nome, Telefone telefone, String telegramChatId) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode estar vazio");
        }
        if (telefone == null) {
            throw new IllegalArgumentException("Telefone não pode ser nulo");
        }
        this.id = UUID.randomUUID();
        this.nome = nome.trim();
        this.telefone = telefone;
        this.telegramChatId = telegramChatId;
        this.criadoEm = LocalDateTime.now();
    }

    private Cliente(UUID id, String nome, Telefone telefone, String telegramChatId, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.telegramChatId = telegramChatId;
        this.criadoEm = criadoEm;
    }

    public static Cliente comId(UUID id, String nome, Telefone telefone, String telegramChatId, LocalDateTime criadoEm) {
        return new Cliente(id, nome, telefone, telegramChatId, criadoEm);
    }
}