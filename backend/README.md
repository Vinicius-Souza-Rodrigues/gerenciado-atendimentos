# Backend — Gerenciador de Atendimentos

Spring Boot 3 com Java 21. Arquitetura Hexagonal com TDD.

## Pré-requisitos

- Java 21+
- PostgreSQL 14+ (via Supabase ou local)
- Docker (para Evolution API local)

## Configuração local

### 1. Variáveis de ambiente

Crie um arquivo `.env` na raiz do backend:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=agendamentos
DB_USER=postgres
DB_PASSWORD=sua_senha
SERVER_PORT=8080
```

### 2. Banco de dados

Execute o script SQL em `src/main/resources/schema.sql` no seu banco PostgreSQL:

```sql
-- Copie e execute o conteúdo de schema.sql no Supabase ou PostgreSQL local
```

### 3. Rodando o backend

```bash
# Com Maven Wrapper (não precisa instalar Maven)
./mvnw spring-boot:run

# Com variáveis de ambiente
DB_HOST=localhost DB_PORT=5432 DB_NAME=agendamentos DB_USER=postgres DB_PASSWORD=postgres ./mvnw spring-boot:run
```

Backend estará disponível em `http://localhost:8080`.

### 4. Testando o healthcheck

```bash
curl http://localhost:8080/health
# Esperado: {"status":"ok"}
```

## Testes

```bash
# Todos os testes
./mvnw test

# Um teste específico
./mvnw test -Dtest=ApplicationTests

# Com cobertura
./mvnw test jacoco:report
```

## Estrutura de diretórios

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/agendamentos/
│   │   │   ├── adapter/
│   │   │   │   ├── inbound/    (WebhookController, RestApiController)
│   │   │   │   └── outbound/   (Repositories, WhatsAppGateway)
│   │   │   ├── domain/          (Entidades, Services, Portas)
│   │   │   ├── config/          (Configurações Spring)
│   │   │   └── Application.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── schema.sql
│   └── test/
│       ├── java/com/agendamentos/
│       └── resources/
│           └── application-test.yml
├── pom.xml
└── README.md
```

## Roadmap

- ✅ **Fase 1**: Setup e infraestrutura
- ⏳ **Fase 2**: Domínio core (TDD)
- ⏳ **Fase 3**: Persistência (Adapters outbound)
- ⏳ **Fase 4**: Webhook e resposta WhatsApp
- ⏳ **Fase 5**: Painel web (Next.js)
- ⏳ **Fase 6**: Deploy
