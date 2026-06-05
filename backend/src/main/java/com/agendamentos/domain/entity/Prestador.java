package com.agendamentos.domain.entity;

import com.agendamentos.domain.valueobject.Telefone;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Prestador {
    UUID id;
    String nomeNegocio;
    Telefone telefoneWhatsApp;
    LocalDateTime criadoEm;

    public Prestador(String nomeNegocio, Telefone telefoneWhatsApp) {
        if (nomeNegocio == null || nomeNegocio.isBlank()) {
            throw new IllegalArgumentException("Nome do negócio não pode estar vazio");
        }
        if (telefoneWhatsApp == null) {
            throw new IllegalArgumentException("Telefone WhatsApp não pode ser nulo");
        }
        this.id = UUID.randomUUID();
        this.nomeNegocio = nomeNegocio.trim();
        this.telefoneWhatsApp = telefoneWhatsApp;
        this.criadoEm = LocalDateTime.now();
    }

    private Prestador(UUID id, String nomeNegocio, Telefone telefoneWhatsApp, LocalDateTime criadoEm) {
        this.id = id;
        this.nomeNegocio = nomeNegocio;
        this.telefoneWhatsApp = telefoneWhatsApp;
        this.criadoEm = criadoEm;
    }

    public static Prestador comId(UUID id, String nomeNegocio, Telefone telefoneWhatsApp, LocalDateTime criadoEm) {
        return new Prestador(id, nomeNegocio, telefoneWhatsApp, criadoEm);
    }
}