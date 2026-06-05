package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ClienteService {
    
    private final ClienteRepositoryPort clienteRepository;

    public Cliente cadastrar(String nome, Telefone telefone) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
        if (telefone == null) {
            throw new IllegalArgumentException("Telefone não pode ser nulo");
        }
        
        Cliente cliente = new Cliente(nome, telefone);
        return clienteRepository.salvar(cliente);
    }

    public Optional<Cliente> buscarPorTelefone(Telefone telefone) {
        if (telefone == null) {
            throw new IllegalArgumentException("Telefone não pode ser nulo");
        }
        return clienteRepository.buscarPorTelefone(telefone);
    }
}