package com.agendamentos.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prestador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestadorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nome_negocio", nullable = false)
    private String nomeNegocio;

    @Column(name = "telefone_whatsapp", nullable = false)
    private String telefoneWhatsApp;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

}
