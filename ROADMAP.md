# ROADMAP — Gerenciador de Consultas
Versão: 1.0 | Data: 2026-06-03

---

## Como usar este roadmap

- Implementar uma fase por vez
- Só avançar para a próxima fase quando o critério de conclusão estiver satisfeito
- Ao pausar: marcar a tarefa atual com `[PAUSED]` e anotar o motivo

---

## Fase 1 — Setup e infraestrutura

**Critério de conclusão:** consigo rodar o backend Spring Boot localmente, ele conecta no Supabase e responde `200 OK` num endpoint de healthcheck. A Evolution API está rodando e apontando para o backend local via webhook.

**Tarefas:**
- [ ] Criar projeto Spring Boot 3 com Java 21 (Spring Initializr: Web, JPA, Validation, Lombok)
- [ ] Configurar conexão com Supabase (application.yml com JDBC URL do Supabase)
- [ ] Criar tabelas no Supabase (`prestador`, `cliente`, `agendamento`)
- [ ] Subir Evolution API via Docker localmente
- [ ] Configurar webhook da Evolution API apontando para `localhost:8080/webhook/mensagem`
- [ ] Criar endpoint `GET /health` que retorna `{ status: "ok" }`
- [ ] Testar: enviar mensagem no WhatsApp e ver o payload chegando no backend (log)

---

## Fase 2 — Domínio core (TDD)

**Critério de conclusão:** consigo rodar `./mvnw test` e todos os testes do domínio passam. As regras de conflito de horário e máquina de estados funcionam sem tocar em banco ou HTTP.

**Tarefas:**
- [ ] Criar entidades: `Agendamento`, `Cliente`, `Prestador`, `HorarioSlot`
- [ ] Criar value objects: `Telefone`, `StatusAgendamento`, `NomeServico`
- [ ] Criar portas (interfaces): `AgendamentoRepositoryPort`, `ClienteRepositoryPort`, `NotificacaoPort`
- [ ] Escrever testes (TDD) para `AgendamentoService`:
  - [ ] Criar agendamento com horário disponível → sucesso
  - [ ] Criar agendamento com conflito de horário → exceção
  - [ ] Criar agendamento no passado → exceção
  - [ ] Cancelar agendamento CONFIRMADO → sucesso
  - [ ] Cancelar agendamento CONCLUIDO → exceção
- [ ] Implementar `AgendamentoService` até todos os testes passarem
- [ ] Escrever testes para `ClienteService` (cadastrar, buscar por telefone)
- [ ] Implementar `ClienteService`

---

## Fase 3 — Persistência (Adapters outbound)

**Critério de conclusão:** consigo criar um agendamento via teste de integração e ele aparece salvo no Supabase.

**Tarefas:**
- [ ] Criar entidades JPA (`AgendamentoEntity`, `ClienteEntity`, `PrestadorEntity`)
- [ ] Implementar `AgendamentoRepositoryAdapter` (implementa `AgendamentoRepositoryPort`)
- [ ] Implementar `ClienteRepositoryAdapter` (implementa `ClienteRepositoryPort`)
- [ ] Escrever teste de integração com Testcontainers (PostgreSQL local)
- [ ] Configurar um prestador fixo no banco (seed — no MVP é single-tenant)

---

## Fase 4 — Webhook e resposta WhatsApp

**Critério de conclusão:** envio "quero agendar amanhã às 10h" no WhatsApp e recebo uma resposta de confirmação (ou de horário indisponível). O agendamento aparece salvo no banco.

**Tarefas:**
- [ ] Implementar `WebhookController` (`POST /webhook/mensagem`)
- [ ] Implementar parser de intenção por palavras-chave (agendar / cancelar / horários)
- [ ] Implementar `WhatsAppGatewayAdapter` (implementa `NotificacaoPort` via Evolution API REST)
- [ ] Escrever testes do `WebhookController` com mocks do `AgendamentoService`
- [ ] Testar fluxo completo: mensagem → webhook → domínio → banco → resposta WhatsApp

---

## Fase 5 — Painel web (Next.js)

**Critério de conclusão:** acesso `http://localhost:3000` e consigo ver a agenda do dia com os agendamentos criados via WhatsApp. Consigo clicar num agendamento e ver o detalhe do cliente.

**Tarefas:**
- [ ] Criar projeto Next.js 14 com App Router
- [ ] Implementar `RestApiController` no Spring Boot (endpoints listados no SDD)
- [ ] Criar página `/` com lista de agendamentos do dia
- [ ] Criar página `/agenda` com visão semanal
- [ ] Criar página `/clientes` com lista e busca
- [ ] Criar página `/clientes/[id]` com histórico do cliente
- [ ] Conectar frontend à API do Spring Boot

---

## Fase 6 — Deploy

**Critério de conclusão:** o sistema está acessível pela internet. O prestador acessa o painel pelo celular, manda mensagem no WhatsApp e o agendamento aparece no painel em tempo real.

**Tarefas:**
- [ ] Deploy do Spring Boot no Railway (configurar variáveis de ambiente)
- [ ] Deploy da Evolution API no Railway (mesmo projeto, serviço separado)
- [ ] Deploy do Next.js na Vercel (apontar API_URL para o Railway)
- [ ] Atualizar webhook da Evolution API para URL de produção
- [ ] Testar fluxo completo em produção

---

## Backlog (pós-MVP)

- Autenticação do prestador no painel web
- Cancelamento e reagendamento pelo cliente via WhatsApp
- App mobile para o prestador (React Native ou Flutter)
- Relatórios básicos (atendimentos por dia/semana, clientes frequentes)
- Interpretação de linguagem natural nas mensagens (LLM)
- Multi-prestador / multi-funcionário
