package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.valueobject.Telefone;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(UUID id);
    Optional<Cliente> buscarPorTelefone(Telefone telefone);
}