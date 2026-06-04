# ADR — Architecture Decision Record
Versão: 1.0 | Data: 2026-06-03

---

## Metodologia

**Escolha:** TDD (Test-Driven Development)

**Justificativa:**
- Integração externa frágil (Evolution API via webhooks) exige contratos bem definidos e testáveis
- Regras de negócio de agendamento (conflito de horário, mudança de status) têm comportamento crítico que não pode regredir
- Arquitetura hexagonal facilita o TDD — o domínio é puro e testável sem depender de banco ou HTTP

**Onde aplicar TDD obrigatoriamente:**
- Processamento do webhook (receber mensagem → criar agendamento)
- Validação de conflito de horário
- Máquina de estados da consulta (PENDENTE → CONFIRMADO → CANCELADO → CONCLUÍDO)
- Serviços do domínio (AgendamentoService, ClienteService)

---

## Arquitetura

**Escolha:** Hexagonal (Ports & Adapters)

**Justificativa:**
- Isola o domínio de agendamento das integrações externas
- Permite trocar a biblioteca de WhatsApp, banco de dados ou framework sem tocar no domínio
- Facilita testes unitários puros do domínio (sem mocks de framework)

**Estrutura de camadas:**

```
Adapters de entrada (Inbound)
  ├── WebhookController     ← recebe eventos da Evolution API
  └── RestApiController     ← API consumida pelo Next.js

Domínio (núcleo isolado)
  ├── AgendamentoService    ← regras de negócio de agendamento
  ├── ClienteService        ← cadastro e histórico
  ├── PrestadorService      ← dados do negócio
  └── Entidades + ValueObjects

Adapters de saída (Outbound)
  ├── AgendamentoRepository ← persiste no Supabase via JPA
  ├── ClienteRepository     ← persiste no Supabase via JPA
  └── WhatsAppGateway       ← envia mensagens via Evolution API REST
```

---

## Stack

| Camada | Tecnologia | Justificativa |
|---|---|---|
| **WhatsApp** | Evolution API (self-hosted) | Open source, expõe REST + webhooks, gratuito |
| **Backend** | Spring Boot 3 (Java 21) | Preferência do time, excelente suporte a hexagonal e TDD |
| **Banco** | Supabase (PostgreSQL) | Free tier generoso, gerenciado, integra via JDBC/JPA |
| **Frontend** | Next.js 14 + React | Preferência do time, free tier na Vercel |
| **Hospedagem backend** | Railway | Suporta Java/Spring, free tier disponível |
| **Hospedagem frontend** | Vercel | Free tier, deploy automático via Git |
| **Testes** | JUnit 5 + Mockito + Testcontainers | Stack padrão Spring, Testcontainers para integração com banco |

---

## Decisões descartadas

| Alternativa | Por que foi descartada |
|---|---|
| Node.js + Express no backend | Substituído por Spring Boot por preferência do time |
| Baileys (Node.js) | Requer backend Node — incompatível com Spring Boot direto |
| SQLite | Sem capacidade de acesso remoto entre serviços; limitado para produção |
| Firebase | Vendor lock-in e free tier limitado para banco relacional |
