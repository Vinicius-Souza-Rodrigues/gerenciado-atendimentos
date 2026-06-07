package com.agendamentos.adapter.outbound.persistence.seed;

import com.agendamentos.adapter.outbound.persistence.jpa.PrestadorJpaRepository;
import com.agendamentos.adapter.outbound.persistence.entity.PrestadorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrestadorSeeder implements CommandLineRunner {

    private final PrestadorJpaRepository repository;

    @Override
    public void run(String... args) {
        try {
            if (repository.count() == 0) {
                var prestador = new PrestadorEntity();
                prestador.setId(UUID.randomUUID());
                prestador.setNomeNegocio("Salão Padrão");
                prestador.setTelefoneWhatsApp("5511999999999");
                prestador.setCriadoEm(LocalDateTime.now());
                repository.save(prestador);
                log.info("Prestador padrão criado com sucesso");
            }
        } catch (Exception e) {
            log.warn("Erro ao criar prestador padrão: {}", e.getMessage());
        }
    }

}
