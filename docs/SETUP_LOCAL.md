# Setup Local — Fase 1

Instruções para rodar o sistema localmente e validar o critério de conclusão da Fase 1.

## Pré-requisitos

- Docker e Docker Compose instalados
- Git

> **Alternativa local (sem Docker):** Java 21+ e Maven 3.9+

## Passo 1: Clonar o repositório

```bash
cd gerenciado-atendimentos
```

## Passo 2: Subir infraestrutura completa (PostgreSQL, Redis, Evolution API, Spring Boot)

```bash
docker-compose up -d
```

Aguarde até que todos os serviços estejam healthy/running:

```bash
docker-compose ps
```

Esperado:
- `agendamentos-postgres` → healthy
- `agendamentos-redis` → running
- `agendamentos-evolution` → running
- `agendamentos-backend` → running

## Passo 3: Verificar banco de dados

As tabelas devem ter sido criadas automaticamente pelo schema.sql:

```bash
# Conectar ao PostgreSQL
docker-compose exec postgres psql -U postgres -d agendamentos -c "\dt"
```

Esperado:
```
       List of relations
 Schema |     Name      | Type  | Owner
--------+---------------+-------+----------
 public | agendamento   | table | postgres
 public | cliente       | table | postgres
 public | prestador     | table | postgres
```

## Passo 4: Testar healthcheck

```bash
curl http://localhost:8080/health
```

Esperado:
```json
{"status":"ok"}
```

## Passo 5: Configurar Evolution API

### 5.1 Acessar dashboard da Evolution API

Abra: `http://localhost:8888`

(Se pedir credenciais, use as padrões ou crie uma conta)

### 5.2 Conectar uma instância WhatsApp

1. Clique em "Criar instância"
2. Dê um nome (ex: `default`)
3. Confirme
4. Você receberá um QR code
5. Escaneie com o celular que tem o WhatsApp que vai usar para testar

### 5.3 Configurar webhook

Após conectar:

1. Vá em "Configurações" da instância
2. Webhook:
   - **URL**: `http://backend:8080/webhook/mensagem` (usando Docker Compose com rede interna)
3. **Eventos**: Marque "message"
4. Salve

## Passo 6: Validar fluxo completo

### 6.1 Enviar mensagem no WhatsApp

Do seu celular, mande uma mensagem para o número WhatsApp conectado na Evolution API.

Exemplo: `"Quero agendar um corte de cabelo amanhã às 10h"`

### 6.2 Verificar logs do backend

```bash
docker-compose logs -f backend
```

Você deverá ver:

```
[WebhookController] Webhook recebido: {...}
```

### 6.3 Verificar banco de dados

```bash
docker-compose exec postgres psql -U postgres -d agendamentos -c "SELECT * FROM agendamento;"
```

## Troubleshooting

### Backend não conecta no banco

Verifique se o PostgreSQL está rodando:

```bash
docker-compose logs postgres
```

Se houver erro, reinicie:

```bash
docker-compose restart postgres
```

### Evolution API não recebe a mensagem

1. Verifique se a instância está conectada ao WhatsApp (status "CONNECTED")
2. Teste enviando uma mensagem da Evolution API para seu número:
   - Dashboard → Enviar mensagem de teste
3. Verifique os logs:

```bash
docker-compose logs evolution
```

### Webhook não dispara

1. Verifique se a URL do webhook está correta (com `host.docker.internal` no Windows/Mac)
2. Teste manualmente:

```bash
curl -X POST http://localhost:8080/webhook/mensagem \
  -H "Content-Type: application/json" \
  -d '{
    "from": "5511999999999",
    "body": "Quero agendar amanhã"
  }'
```

Esperado: Status 200 OK

## Parar tudo

```bash
docker-compose down
```

Para limpar volumes também:

```bash
docker-compose down -v
```

## Próxima etapa

Quando tudo estiver funcionando, passe para **Fase 2: Domínio core (TDD)**.
