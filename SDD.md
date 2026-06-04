# SDD — System Design Document
Versão: 1.0 | Data: 2026-06-03

---

## Visão geral do sistema

Sistema de agendamento para comércios locais com serviço por hora marcada. O cliente agenda pelo WhatsApp; o prestador gerencia pelo painel web. A integração com WhatsApp é feita via Evolution API (self-hosted), que envia webhooks ao backend Spring Boot quando mensagens chegam.

### Fluxo principal

```
Cliente envia mensagem WhatsApp
        ↓
Evolution API (self-hosted)
        ↓ webhook POST
Spring Boot — WebhookController
        ↓
AgendamentoService (domínio)
  - interpreta intenção da mensagem
  - valida horário disponível
  - cria ou rejeita agendamento
        ↓
AgendamentoRepository → Supabase (PostgreSQL)
        ↓
WhatsAppGateway → Evolution API REST → resposta ao cliente
        ↑
Next.js (painel web) ← REST API ← Spring Boot
```

### Diagrama de blocos

```
┌──────────────────────────────────────────────────────────────┐
│                        ADAPTERS INBOUND                      │
│  ┌──────────────────────┐   ┌──────────────────────────────┐ │
│  │  WebhookController   │   │     RestApiController        │ │
│  │  POST /webhook/msg   │   │  GET  /agendamentos          │ │
│  │                      │   │  POST /agendamentos           │ │
│  └──────────┬───────────┘   └──────────────┬───────────────┘ │
└─────────────┼────────────────────────────── ┼────────────────┘
              ↓                               ↓
┌──────────────────────────────────────────────────────────────┐
│                         DOMÍNIO                              │
│  ┌──────────────────┐  ┌────────────────┐  ┌─────────────┐  │
│  │AgendamentoService│  │ClienteService  │  │PrestadorSvc │  │
│  │- criar()         │  │- cadastrar()   │  │- getConfig()│  │
│  │- cancelar()      │  │- buscarPorTel()│  │             │  │
│  │- listar()        │  │- historico()   │  │             │  │
│  └────────┬─────────┘  └───────┬────────┘  └──────┬──────┘  │
│           │                    │                   │         │
│  ┌────────▼──────────────────────────────────────────────┐   │
│  │               Entidades + Value Objects               │   │
│  │  Agendamento · Cliente · Prestador · HorarioSlot      │   │
│  │  StatusAgendamento (enum) · Telefone · NomeServico    │   │
│  └───────────────────────────────────────────────────────┘   │
└──────────────┬─────────────────────────────────────────────-─┘
               ↓
┌──────────────────────────────────────────────────────────────┐
│                       ADAPTERS OUTBOUND                      │
│  ┌──────────────────────┐   ┌──────────────────────────────┐ │
│  │  AgendamentoRepo     │   │     WhatsAppGateway          │ │
│  │  ClienteRepo         │   │  enviarConfirmacao()         │ │
│  │  (JPA → Supabase)    │   │  enviarRejeicao()            │ │
│  └──────────────────────┘   └──────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

---

## Módulos

### Módulo 1 — Domínio de Agendamento

**Responsabilidade:** Núcleo das regras de negócio. Não depende de framework, banco ou HTTP.

**Entidades:**

| Entidade | Campos principais |
|---|---|
| `Agendamento` | id, clienteId, prestadorId, dataHora, servico, status, criadoEm |
| `Cliente` | id, nome, telefone, criadoEm |
| `Prestador` | id, nomeNegocio, telefoneWhatsApp, horariosDisponiveis |
| `HorarioSlot` | dataHora, duracao, disponivel |

**Value Objects:**
- `Telefone` — valida formato, normaliza DDD
- `StatusAgendamento` — enum: `PENDENTE`, `CONFIRMADO`, `CANCELADO`, `CONCLUIDO`
- `NomeServico` — string não vazia, max 100 chars

**Regras invioláveis do domínio:**
1. Não pode haver dois agendamentos confirmados no mesmo horário para o mesmo prestador
2. Agendamento só pode ser criado com horário futuro
3. Cancelamento de agendamento `CONCLUIDO` é proibido
4. Toda mudança de status é registrada com timestamp

**Portas (interfaces):**
- `AgendamentoRepositoryPort` — salvar, buscar, listar
- `ClienteRepositoryPort` — salvar, buscarPorTelefone
- `NotificacaoPort` — enviarConfirmacao(telefone, agendamento)

---

### Módulo 2 — Webhook (Adapter Inbound)

**Responsabilidade:** Receber eventos da Evolution API, interpretar a intenção da mensagem e acionar o domínio.

**Endpoint:** `POST /webhook/mensagem`

**Fluxo:**
1. Recebe payload da Evolution API (remetente, texto da mensagem, timestamp)
2. Extrai telefone do remetente e texto
3. Interpreta intenção: agendar / cancelar / consultar horários
4. Aciona `AgendamentoService` com os dados extraídos
5. Resultado dispara notificação de volta ao cliente

**Interpretação de mensagem (MVP — baseada em palavras-chave):**
- "agendar", "marcar", "quero" → intenção de agendar
- "cancelar", "desmarcar" → intenção de cancelar
- "horários", "disponível" → consulta de disponibilidade

> Evolução futura: substituir palavras-chave por processamento de linguagem natural (LLM).

---

### Módulo 3 — REST API (Adapter Inbound)

**Responsabilidade:** Expor endpoints HTTP para o painel web Next.js.

**Endpoints do MVP:**

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/agendamentos` | Lista agendamentos com filtro de data |
| `GET` | `/agendamentos/{id}` | Detalhe de um agendamento |
| `PATCH` | `/agendamentos/{id}/status` | Atualiza status manualmente |
| `GET` | `/clientes` | Lista clientes cadastrados |
| `GET` | `/clientes/{id}/historico` | Histórico de agendamentos do cliente |

---

### Módulo 4 — WhatsApp Gateway (Adapter Outbound)

**Responsabilidade:** Enviar mensagens de resposta ao cliente via Evolution API REST.

**Operações:**
- `enviarConfirmacao(telefone, agendamento)` — confirma horário agendado
- `enviarRejeicao(telefone, motivo)` — informa horário indisponível
- `enviarConsultaHorarios(telefone, slots)` — lista horários disponíveis

**Contrato de falha:** se o envio falhar, o agendamento já está salvo no banco — a notificação é best-effort no MVP.

---

### Módulo 5 — Painel Web (Next.js)

**Responsabilidade:** Interface do prestador para visualizar e gerenciar agendamentos.

**Páginas do MVP:**

| Página | Função |
|---|---|
| `/` | Agenda do dia com lista de agendamentos |
| `/agenda` | Visão semanal da agenda |
| `/clientes` | Lista de clientes com busca por nome/telefone |
| `/clientes/[id]` | Perfil do cliente com histórico |

**Autenticação:** sem autenticação no MVP (acesso direto pela URL — adequado pra uso local). Autenticação entra em fase posterior.

---

## Banco de dados — Modelo simplificado

```sql
prestador (id, nome_negocio, telefone_whatsapp, criado_em)

cliente (id, nome, telefone, criado_em)

agendamento (
  id,
  prestador_id → prestador.id,
  cliente_id   → cliente.id,
  data_hora,
  servico,
  status,      -- PENDENTE | CONFIRMADO | CANCELADO | CONCLUIDO
  criado_em,
  atualizado_em
)
```

---

## Contratos entre módulos (regras invioláveis)

1. O `WebhookController` **nunca** acessa o banco diretamente — sempre via `AgendamentoService`
2. O `AgendamentoService` **nunca** importa classes de Spring, JPA ou HTTP
3. O `WhatsAppGateway` **nunca** contém regras de negócio — só traduz e envia
4. Toda porta (interface) fica no pacote `domain.port` — os adapters implementam, o domínio declara
