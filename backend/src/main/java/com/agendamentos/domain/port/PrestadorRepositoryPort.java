package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.Prestador;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PrestadorRepositoryPort {
    Prestador salvar(Prestador prestador);
    Optional<Prestador> buscarPorId(UUID id);
    List<Prestador> listarTodos();
}
