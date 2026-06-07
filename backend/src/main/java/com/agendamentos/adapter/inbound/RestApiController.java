package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.AtualizarStatusRequest;
import com.agendamentos.adapter.inbound.dto.AgendamentoResponse;
import com.agendamentos.adapter.inbound.dto.ClienteResponse;
import com.agendamentos.adapter.inbound.dto.CriarAgendamentoRequest;
import com.agendamentos.adapter.inbound.dto.CriarClienteRequest;
import com.agendamentos.adapter.inbound.dto.CriarPrestadorRequest;
import com.agendamentos.adapter.inbound.dto.PrestadorResponse;
import com.agendamentos.domain.entity.Agendamento;
import com.agendamentos.domain.entity.Cliente;
import com.agendamentos.domain.entity.Prestador;
import com.agendamentos.domain.port.AgendamentoRepositoryPort;
import com.agendamentos.domain.port.ClienteRepositoryPort;
import com.agendamentos.domain.port.PrestadorRepositoryPort;
import com.agendamentos.domain.service.AgendamentoService;
import com.agendamentos.domain.service.ClienteService;
import com.agendamentos.domain.service.PrestadorService;
import com.agendamentos.domain.valueobject.NomeServico;
import com.agendamentos.domain.valueobject.StatusAgendamento;
import com.agendamentos.domain.valueobject.Telefone;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestApiController {

    private final AgendamentoService agendamentoService;
    private final ClienteService clienteService;
    private final PrestadorService prestadorService;
    private final AgendamentoRepositoryPort agendamentoRepository;
    private final ClienteRepositoryPort clienteRepository;
    private final PrestadorRepositoryPort prestadorRepository;

    @GetMapping("/agendamentos")
    public ResponseEntity<List<AgendamentoResponse>> listarAgendamentos() {
        var agendamentos = agendamentoRepository.listarTodos();
        var responses = agendamentos.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/agendamentos/{id}")
    public ResponseEntity<AgendamentoResponse> buscarAgendamento(@PathVariable UUID id) {
        return agendamentoRepository.buscarPorId(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/agendamentos")
    public ResponseEntity<AgendamentoResponse> criarAgendamento(
            @RequestBody CriarAgendamentoRequest request) {
        var agendamento = agendamentoService.criar(
                request.clienteId(),
                request.prestadorId(),
                request.dataHora(),
                new NomeServico(request.servico())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(agendamento));
    }

    @PatchMapping("/agendamentos/{id}/status")
    public ResponseEntity<AgendamentoResponse> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody AtualizarStatusRequest request) {
        var agendamento = agendamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        var statusEnum = StatusAgendamento.valueOf(request.status());
        Agendamento atualizado = switch (statusEnum) {
            case CONFIRMADO -> agendamento.confirmar();
            case CANCELADO -> agendamento.cancelar();
            case CONCLUIDO -> agendamento.concluir();
            case PENDENTE -> agendamento;
        };

        var salvo = agendamentoRepository.salvar(atualizado);
        return ResponseEntity.ok(toResponse(salvo));
    }

    @GetMapping("/prestadores")
    public ResponseEntity<List<PrestadorResponse>> listarPrestadores() {
        var prestadores = prestadorRepository.listarTodos();
        var responses = prestadores.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/prestadores")
    public ResponseEntity<PrestadorResponse> criarPrestador(
            @RequestBody CriarPrestadorRequest request) {
        var prestador = prestadorService.criar(
                request.nomeNegocio(),
                new Telefone(request.telefoneWhatsApp())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(prestador));
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        var clientes = clienteRepository.listarTodos();
        var responses = clientes.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/clientes")
    public ResponseEntity<ClienteResponse> criarCliente(
            @RequestBody CriarClienteRequest request) {
        var cliente = clienteService.cadastrar(
                request.nome(),
                new Telefone(request.telefone())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(cliente));
    }

    @GetMapping("/clientes/{id}/historico")
    public ResponseEntity<List<AgendamentoResponse>> historicoCliente(@PathVariable UUID id) {
        var agendamentos = agendamentoRepository.listarPorCliente(id);
        var responses = agendamentos.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private AgendamentoResponse toResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getClienteId(),
                agendamento.getPrestadorId(),
                agendamento.getDataHora(),
                agendamento.getServico().getValor(),
                agendamento.getStatus().toString(),
                agendamento.getCriadoEm(),
                agendamento.getAtualizadoEm()
        );
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone().getNumero(),
                cliente.getCriadoEm()
        );
    }

    private PrestadorResponse toResponse(Prestador prestador) {
        return new PrestadorResponse(
                prestador.getId(),
                prestador.getNomeNegocio(),
                prestador.getTelefoneWhatsApp().getNumero(),
                prestador.getCriadoEm()
        );
    }

}
