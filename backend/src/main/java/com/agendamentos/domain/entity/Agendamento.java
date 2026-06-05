package com.agendamentos.domain.entity;

import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Agendamento {
    UUID id;
    UUID clienteId;
    UUID prestadorId;
    LocalDateTime dataHora;
    NomeServico servico;
    StatusAgendamento status;
    LocalDateTime criadoEm;
    LocalDateTime atualizadoEm;

    public Agendamento(UUID clienteId, UUID prestadorId, LocalDateTime dataHora, NomeServico servico) {
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID não pode ser nulo");
        }
        if (prestadorId == null) {
            throw new IllegalArgumentException("Prestador ID não pode ser nulo");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("Data/hora não pode ser nula");
        }
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não pode ser nulo");
        }
        
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.prestadorId = prestadorId;
        this.dataHora = dataHora;
        this.servico = servico;
        this.status = StatusAgendamento.PENDENTE;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    private Agendamento(UUID id, UUID clienteId, UUID prestadorId, LocalDateTime dataHora, 
                        NomeServico servico, StatusAgendamento status, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        this.id = id;
        this.clienteId = clienteId;
        this.prestadorId = prestadorId;
        this.dataHora = dataHora;
        this.servico = servico;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static Agendamento comId(UUID id, UUID clienteId, UUID prestadorId, LocalDateTime dataHora,
                                     NomeServico servico, StatusAgendamento status, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        return new Agendamento(id, clienteId, prestadorId, dataHora, servico, status, criadoEm, atualizadoEm);
    }

    public Agendamento confirmar() {
        if (this.status == StatusAgendamento.CONFIRMADO) {
            return this;
        }
        return new Agendamento(this.id, this.clienteId, this.prestadorId, this.dataHora, 
                              this.servico, StatusAgendamento.CONFIRMADO, this.criadoEm, LocalDateTime.now());
    }

    public Agendamento cancelar() {
        if (this.status == StatusAgendamento.CONCLUIDO) {
            throw new IllegalStateException("Não é possível cancelar agendamento concluído");
        }
        return new Agendamento(this.id, this.clienteId, this.prestadorId, this.dataHora,
                              this.servico, StatusAgendamento.CANCELADO, this.criadoEm, LocalDateTime.now());
    }

    public Agendamento concluir() {
        return new Agendamento(this.id, this.clienteId, this.prestadorId, this.dataHora,
                              this.servico, StatusAgendamento.CONCLUIDO, this.criadoEm, LocalDateTime.now());
    }

    public boolean estahNoFuturo() {
        return dataHora.isAfter(LocalDateTime.now());
    }

    public boolean estahConfirmado() {
        return status == StatusAgendamento.CONFIRMADO;
    }
}