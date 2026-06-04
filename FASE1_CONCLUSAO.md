# Fase 1 — Setup e Infraestrutura ✅

**Status:** Implementado e pronto para validação

**Data:** 2026-06-03

---

## Tarefas Completadas

- ✅ **Criar projeto Spring Boot 3 com Java 21**
  - Arquivos: `backend/pom.xml`, `backend/src/main/java/com/agendamentos/Application.java`
  - Dependências: Spring Web, Spring Data JPA, Validation, Lombok, PostgreSQL Driver, JUnit 5, Mockito, Testcontainers
  - Maven Wrapper configurado (`mvnw.cmd`, `.mvn/wrapper/`)

- ✅ **Configurar conexão com Supabase (ou PostgreSQL local)**
  - Arquivo: `backend/src/main/resources/application.yml`
  - Suporta variáveis de ambiente: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`

- ✅ **Criar tabelas no Supabase**
  - Arquivo: `backend/src/main/resources/schema.sql`
  - Tabelas: `prestador`, `cliente`, `agendamento`
  - Índices para performance
  - Entidades JPA mapeadas: `PrestadorEntity`, `ClienteEntity`, `AgendamentoEntity`
  - Repositórios: `PrestadorJpaRepository`, `ClienteJpaRepository`, `AgendamentoJpaRepository`

- ✅ **Subir Evolution API via Docker**
  - Arquivo: `docker-compose.yml`
  - Serviço: `evolution` (imagem: `atendai/evolution-api:latest`)
  - Também inclui: PostgreSQL, Redis, Spring Boot Backend
  - Rede customizada: `agendamentos-net` para comunicação entre serviços

- ✅ **Configurar webhook da Evolution API**
  - URL configurável: `http://backend:8080/webhook/mensagem` (dentro da rede Docker)
  - Implementado: `WebhookController` com endpoint `POST /webhook/mensagem`
  - Recebe e loga payloads (implementação completa será na Fase 4)

- ✅ **Criar endpoint GET /health**
  - Arquivo: `backend/src/main/adapter/inbound/HealthController.java`
  - Retorna: `{"status":"ok"}`
  - Testado em: `ApplicationTests.java`

---

## Estrutura do Projeto

```
gerenciado-atendimentos/
├── backend/
│   ├── pom.xml                                    (dependências Maven)
│   ├── mvnw / mvnw.cmd                           (Maven Wrapper)
│   ├── .mvn/wrapper/                             (Maven config)
│   ├── Dockerfile                                (build para Docker)
│   ├── README.md                                 (instruções do backend)
│   └── src/
│       ├── main/
│       │   ├── java/com/agendamentos/
│       │   │   ├── Application.java
│       │   │   ├── adapter/
│       │   │   │   ├── inbound/
│       │   │   │   │   ├── HealthController.java
│       │   │   │   │   └── WebhookController.java
│       │   │   │   └── outbound/
│       │   │   │       ├── PrestadorEntity.java
│       │   │   │       ├── ClienteEntity.java
│       │   │   │       ├── AgendamentoEntity.java
│       │   │   │       ├── PrestadorJpaRepository.java
│       │   │   │       ├── ClienteJpaRepository.java
│       │   │   │       └── AgendamentoJpaRepository.java
│       │   └── resources/
│       │       ├── application.yml                (config com env vars)
│       │       └── schema.sql                    (DDL do banco)
│       └── test/
│           ├── java/com/agendamentos/
│           │   └── ApplicationTests.java
│           └── resources/
│               └── application-test.yml          (H2 para testes)
├── docker-compose.yml                           (infraestrutura completa)
├── SETUP_LOCAL.md                               (instruções de execução)
└── FASE1_CONCLUSAO.md                           (este arquivo)
```

---

## Critério de Conclusão ✅

Valide que todos os itens abaixo funcionam:

### 1️⃣ Backend inicia sem erros

```bash
docker-compose up -d
docker-compose logs backend | grep "Started Application"
```

**Esperado:** Mensagem `Started Application in X.XXX seconds`

### 2️⃣ Banco de dados conecta

```bash
docker-compose logs backend | grep "HikariPool"
```

**Esperado:** Log de conexão com sucesso

### 3️⃣ Endpoint /health responde OK

```bash
curl http://localhost:8080/health
```

**Esperado:**
```json
{"status":"ok"}
```

### 4️⃣ Tabelas criadas no banco

```bash
docker-compose exec postgres psql -U postgres -d agendamentos -c "\dt"
```

**Esperado:**
```
       List of relations
 Schema |     Name      | Type  | Owner
--------+---------------+-------+----------
 public | agendamento   | table | postgres
 public | cliente       | table | postgres
 public | prestador     | table | postgres
(3 rows)
```

### 5️⃣ Evolution API conecta ao webhook

```bash
curl -X POST http://localhost:8080/webhook/mensagem \
  -H "Content-Type: application/json" \
  -d '{"from":"5511999999999","body":"teste"}'
```

**Esperado:** Status 200 OK
```json
{"status":"recebido"}
```

### 6️⃣ Webhook registra a mensagem nos logs

```bash
docker-compose logs backend | grep "Webhook recebido"
```

**Esperado:** Log da mensagem recebida

---

## Como rodar o setup

### Passo 1: Verificar pré-requisitos

- ✅ Docker instalado: `docker --version`
- ✅ Docker Compose: `docker-compose --version`

### Passo 2: Executar

```bash
cd gerenciado-atendimentos
docker-compose up -d
```

### Passo 3: Validar (veja seção "Critério de Conclusão" acima)

---

## Próximas Fases

- ⏳ **Fase 2**: Domínio core (TDD) — Entidades de negócio, Services, testes
- ⏳ **Fase 3**: Persistência — Repositories com Testcontainers
- ⏳ **Fase 4**: Webhook e WhatsApp — Parser de intenção, resposta
- ⏳ **Fase 5**: Painel web (Next.js)
- ⏳ **Fase 6**: Deploy (Railway + Vercel)

---

## Troubleshooting

### Backend não inicia

```bash
docker-compose logs backend
```

Procure por erros de conexão com banco ou porta em uso.

### PostgreSQL falha na inicialização

```bash
docker-compose down -v
docker-compose up -d
```

Limpar volumes e reiniciar.

### Webhook não responde

Certifique-se de que a URL no Evolution API está: `http://backend:8080/webhook/mensagem`

---

## Notas

- Todas as dependências estão no `docker-compose.yml`
- Backend roda em `http://localhost:8080`
- Evolution API dashboard em `http://localhost:8888`
- PostgreSQL em `localhost:5432` (credentials no docker-compose.yml)
- Rede interna `agendamentos-net` permite que os serviços se comuniquem
