package com.agendamentos.domain.port;

import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.valueobject.Telefone;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ClienteRepositoryPort {
    Cliente salvar(Cliente cliente);
    Optional<Cliente> buscarPorId(UUID id);
    Optional<Cliente> buscarPorTelefone(Telefone telefone);
    Optional<Cliente> buscarPorTelegramChatId(String chatId);
    List<Cliente> listarTodos();
    List<Cliente> listarPorPrestador(UUID prestadorId);
}