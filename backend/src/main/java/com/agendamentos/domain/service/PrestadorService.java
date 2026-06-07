package com.agendamentos.domain.service;

import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrestadorService {

    private final PrestadorRepositoryPort prestadorRepository;

    public Prestador criar(String nomeNegocio, Telefone telefoneWhatsApp) {
        if (nomeNegocio == null || nomeNegocio.isBlank()) {
            throw new IllegalArgumentException("Nome do negócio não pode estar vazio");
        }
        if (telefoneWhatsApp == null) {
            throw new IllegalArgumentException("Telefone WhatsApp não pode ser nulo");
        }

        Prestador prestador = new Prestador(nomeNegocio, telefoneWhatsApp);
        return prestadorRepository.salvar(prestador);
    }
}
