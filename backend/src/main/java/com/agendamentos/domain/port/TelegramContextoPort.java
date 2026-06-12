package com.agendamentos.domain.port;

import java.util.Optional;
import java.util.UUID;

public interface TelegramContextoPort {
    void vincular(String chatId, UUID prestadorId);
    Optional<UUID> buscarPrestadorId(String chatId);
}
