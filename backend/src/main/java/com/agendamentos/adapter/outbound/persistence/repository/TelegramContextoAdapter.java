package com.agendamentos.adapter.outbound.persistence.repository;

import com.agendamentos.adapter.outbound.persistence.entity.TelegramContextoEntity;
import com.agendamentos.adapter.outbound.persistence.jpa.TelegramContextoJpaRepository;
import com.agendamentos.domain.port.TelegramContextoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TelegramContextoAdapter implements TelegramContextoPort {

    private final TelegramContextoJpaRepository repository;

    @Override
    public void vincular(String chatId, UUID prestadorId) {
        var entity = new TelegramContextoEntity();
        entity.setChatId(chatId);
        entity.setPrestadorId(prestadorId);
        entity.setAtualizadoEm(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public Optional<UUID> buscarPrestadorId(String chatId) {
        return repository.findById(chatId).map(TelegramContextoEntity::getPrestadorId);
    }
}
