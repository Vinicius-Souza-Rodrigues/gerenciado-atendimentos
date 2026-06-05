package com.agendamentos.adapter.outbound.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "prestador_id", nullable = false, columnDefinition = "uuid")
    private UUID prestadorId;

    @Column(name = "cliente_id", nullable = false, columnDefinition = "uuid")
    private UUID clienteId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "servico", nullable = false)
    private String servico;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.criadoEm = now;
        this.atualizadoEm = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public enum StatusAgendamento {
        PENDENTE,
        CONFIRMADO,
        CANCELADO,
        CONCLUIDO
    }

}
