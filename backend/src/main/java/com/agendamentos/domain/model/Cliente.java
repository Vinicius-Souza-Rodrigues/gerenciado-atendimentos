package com.agendamentos.domain.model;

import com.agendamentos.domain.valueobject.Telefone;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Cliente {

    private final UUID id;
    private final String nome;
    private final Telefone telefone;
    private final LocalDateTime criadoEm;

}
