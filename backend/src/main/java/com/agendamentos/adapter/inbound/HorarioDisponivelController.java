package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.AtualizarHorariosRequest;
import com.agendamentos.adapter.inbound.dto.HorarioDisponivelResponse;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.service.HorarioDisponivelService;
import com.agendamentos.domain.service.HorarioDisponivelService.HorarioDisponivelDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prestadores/{prestadorId}/horarios")
@RequiredArgsConstructor
@Slf4j
public class HorarioDisponivelController {

    private final HorarioDisponivelService horarioService;
    private final PrestadorRepositoryPort prestadorRepository;

    @GetMapping
    public ResponseEntity<List<HorarioDisponivelResponse>> listarHorarios(
            @PathVariable UUID prestadorId) {
        var prestador = prestadorRepository.buscarPorId(prestadorId)
                .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

        var horarios = horarioService.listarHorariosPrestador(prestadorId);

        var response = horarios.stream()
            .map(h -> new HorarioDisponivelResponse(
                h.getId(),
                h.getDiaDaSemana().name(),
                h.getHoraInicio(),
                h.getHoraFim(),
                h.isAtivo()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<List<HorarioDisponivelResponse>> atualizarHorarios(
            @PathVariable UUID prestadorId,
            @RequestBody AtualizarHorariosRequest request) {
        var prestador = prestadorRepository.buscarPorId(prestadorId)
                .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

        var horariosDTO = request.horarios().stream()
            .map(h -> new HorarioDisponivelDTO(
                DayOfWeek.valueOf(h.diaDaSemana().toUpperCase()),
                h.horaInicio(),
                h.horaFim(),
                h.ativo()
            ))
            .toList();

        var atualizado = horarioService.atualizarHorariosPrestador(prestadorId, horariosDTO);

        var response = atualizado.stream()
            .map(h -> new HorarioDisponivelResponse(
                h.getId(),
                h.getDiaDaSemana().name(),
                h.getHoraInicio(),
                h.getHoraFim(),
                h.isAtivo()
            ))
            .toList();

        log.info("Horários do prestador {} atualizados", prestadorId);
        return ResponseEntity.ok(response);
    }
}
