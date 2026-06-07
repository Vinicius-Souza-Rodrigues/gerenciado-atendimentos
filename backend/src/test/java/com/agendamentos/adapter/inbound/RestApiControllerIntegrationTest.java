package com.agendamentos.adapter.inbound;

import com.agendamentos.adapter.inbound.dto.CriarAgendamentoRequest;
import com.agendamentos.adapter.inbound.dto.AtualizarStatusRequest;
import com.agendamentos.adapter.outbound.persistence.entity.ClienteEntity;
import com.agendamentos.adapter.outbound.persistence.entity.PrestadorEntity;
import com.agendamentos.adapter.outbound.persistence.jpa.ClienteJpaRepository;
import com.agendamentos.adapter.outbound.persistence.jpa.PrestadorJpaRepository;
import com.agendamentos.adapter.outbound.persistence.jpa.AgendamentoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Disabled("Teste de integração desabilitado - requer Docker ativo")
class RestApiControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("agendamentos_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrestadorJpaRepository prestadorRepository;

    @Autowired
    private ClienteJpaRepository clienteRepository;

    @Autowired
    private AgendamentoJpaRepository agendamentoRepository;

    private UUID prestadorId;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        prestadorRepository.deleteAll();

        var prestador = new PrestadorEntity();
        prestador.setNomeNegocio("Salão Teste");
        prestador.setTelefoneWhatsApp("5511999999999");
        var saved = prestadorRepository.save(prestador);
        prestadorId = saved.getId();

        var cliente = new ClienteEntity();
        cliente.setNome("João Silva");
        cliente.setTelefone("11987654321");
        var savedCliente = clienteRepository.save(cliente);
        clienteId = savedCliente.getId();
    }

    @Test
    void deveCriarAgendamentoComSucesso() throws Exception {
        var request = new CriarAgendamentoRequest(
                clienteId,
                prestadorId,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                "Corte de Cabelo"
        );

        mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId", is(clienteId.toString())))
                .andExpect(jsonPath("$.prestadorId", is(prestadorId.toString())))
                .andExpect(jsonPath("$.servico", is("Corte de Cabelo")))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    void deveBuscarAgendamentoPorId() throws Exception {
        var request = new CriarAgendamentoRequest(
                clienteId,
                prestadorId,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                "Corte de Cabelo"
        );

        var response = mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var agendamentoId = objectMapper.readTree(response.getResponse().getContentAsString())
                .get("id").asText();

        mockMvc.perform(get("/api/agendamentos/{id}", agendamentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(agendamentoId)))
                .andExpect(jsonPath("$.status", is("PENDENTE")));
    }

    @Test
    void deveAtualizarStatusAgendamento() throws Exception {
        var request = new CriarAgendamentoRequest(
                clienteId,
                prestadorId,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                "Corte de Cabelo"
        );

        var createResponse = mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var agendamentoId = objectMapper.readTree(createResponse.getResponse().getContentAsString())
                .get("id").asText();

        var updateRequest = new AtualizarStatusRequest("CONFIRMADO");

        mockMvc.perform(patch("/api/agendamentos/{id}/status", agendamentoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")));
    }

    @Test
    void deveRetornarHistoricoDoCliente() throws Exception {
        var request = new CriarAgendamentoRequest(
                clienteId,
                prestadorId,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                "Corte de Cabelo"
        );

        mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/clientes/{id}/historico", clienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clienteId", is(clienteId.toString())));
    }

    @Test
    void deveLancarExcecaoAoCriarAgendamentoNoPastado() throws Exception {
        var request = new CriarAgendamentoRequest(
                clienteId,
                prestadorId,
                LocalDateTime.now().minusDays(1),
                "Corte de Cabelo"
        );

        mockMvc.perform(post("/api/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
