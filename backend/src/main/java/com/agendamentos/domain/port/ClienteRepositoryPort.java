package com.agendamentos.domain.port;

import com.agendamentos.domain.model.Cliente;
import com.agendamentos.domain.valueobject.Telefone;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorTelefone(Telefone telefone);

    Optional<Cliente> buscarPorId(UUID id);

}
