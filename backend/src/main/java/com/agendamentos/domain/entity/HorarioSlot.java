package com.agendamentos.domain.entity;

import lombok.Value;
import java.time.LocalDateTime;
import java.time.Duration;

@Value
public class HorarioSlot {
    LocalDateTime dataHora;
    Duration duracao;

    public HorarioSlot(LocalDateTime dataHora, Duration duracao) {
        if (dataHora == null) {
            throw new IllegalArgumentException("Data/hora não pode ser nula");
        }
        if (duracao == null) {
            throw new IllegalArgumentException("Duração não pode ser nula");
        }
        if (duracao.isNegative() || duracao.isZero()) {
            throw new IllegalArgumentException("Duração deve ser positiva");
        }
        this.dataHora = dataHora;
        this.duracao = duracao;
    }

    public LocalDateTime getFim() {
        return dataHora.plus(duracao);
    }

    public boolean sobrepoe(HorarioSlot outro) {
        if (outro == null) return false;
        return !this.getFim().isBefore(outro.dataHora) && 
               !this.dataHora.isAfter(outro.getFim());
    }
}