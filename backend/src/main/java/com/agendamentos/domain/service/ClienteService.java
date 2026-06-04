package com.agendamentos.domain.service;

import com.agendamentos.domain.model.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class ClienteService {

    private final ClienteRepositoryPort clienteRepo;

    public ClienteService(ClienteRepositoryPort clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public Cliente cadastrar(String nome, String telefone) {
        var tel = new Telefone(telefone);

        return clienteRepo.buscarPorTelefone(tel)
                .orElseGet(() -> {
                    var novo = Cliente.builder()
                            .id(UUID.randomUUID())
                            .nome(nome)
                            .telefone(tel)
                            .criadoEm(LocalDateTime.now())
                            .build();
                    return clienteRepo.salvar(novo);
                });
    }

    public Optional<Cliente> buscarPorTelefone(String telefone) {
        return clienteRepo.buscarPorTelefone(new Telefone(telefone));
    }

}
