package com.agendamentos.domain.model;

import com.agendamentos.domain.exception.CancelamentoInvalidoException;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Agendamento {

    private final UUID id;
    private final UUID clienteId;
    private final UUID prestadorId;
    private final LocalDateTime dataHora;
    private final NomeServico servico;
    private final StatusAgendamento status;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    public Agendamento cancelar() {
        if (status == StatusAgendamento.CONCLUIDO) {
            throw new CancelamentoInvalidoException("Agendamento concluído não pode ser cancelado");
        }
        return toBuilder()
                .status(StatusAgendamento.CANCELADO)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public Agendamento confirmar() {
        return toBuilder()
                .status(StatusAgendamento.CONFIRMADO)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    public Agendamento concluir() {
        return toBuilder()
                .status(StatusAgendamento.CONCLUIDO)
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

}
