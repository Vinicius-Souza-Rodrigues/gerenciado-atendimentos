# Configuração Telegram

## Como testar notificações via Telegram

### Passo 1: Criar um Bot no Telegram

1. Abra o Telegram e procure por `@BotFather`
2. Inicie uma conversa com `/start`
3. Digite `/newbot`
4. Escolha um nome para o bot (ex: "Agendamentos Bot")
5. Escolha um username único (ex: `meu_bot_agendamentos`)
6. **Copie o token gerado** (exemplo: `123456789:ABCdefGHIjklmnoPQRstuvWXYz`)

### Passo 2: Pegar seu Chat ID

1. Procure por `@userinfobot` no Telegram
2. Inicie uma conversa com `/start`
3. O bot mostrará seu **ID** (número como `123456789`)

> **Nota:** Este é o Chat ID que você vai usar no número do telefone do cliente durante testes

### Passo 3: Configurar as Variáveis de Ambiente

**Opção A: Variáveis de ambiente do sistema**

```bash
# Windows (PowerShell)
$env:TELEGRAM_BOT_TOKEN="seu_token_aqui"

# Linux/Mac
export TELEGRAM_BOT_TOKEN="seu_token_aqui"
```

**Opção B: Via arquivo `application-dev.yml`**

```yaml
telegram:
  enabled: true
  bot-token: "123456789:ABCdefGHIjklmnoPQRstuvWXYz"
```

### Passo 4: Habilitar Telegram na aplicação

```yaml
telegram:
  enabled: true
  bot-token: ${TELEGRAM_BOT_TOKEN}
```

### Passo 5: Testar a integração

1. Crie um agendamento via API com um cliente cujo telefone seja seu Chat ID:

```bash
POST /agendamentos
{
  "clienteId": "seu-cliente-id",
  "prestadorId": "seu-prestador-id",
  "dataHora": "2026-06-15T14:00:00",
  "servico": "Corte de cabelo"
}
```

2. Você deve receber uma mensagem no Telegram!

## Estrutura da integração

```
TelegramGatewayPort (interface)
├── TelegramGateway (implementação)
│   └── Usa RestTemplate para chamar API Telegram
└── TelegramService (lógica de negócio)
    └── Integrado no AgendamentoService
```

## Tipos de notificação

1. **Agendamento Criado:** Enviado quando um novo agendamento é criado
2. **Status Atualizado:** Enviado quando status do agendamento muda

## Troubleshooting

| Problema | Solução |
|----------|---------|
| Mensagem não envia | Verifique se `telegram.enabled: true` no `application-dev.yml` |
| Token inválido | Copie o token completo do @BotFather |
| Chat ID errado | Use `/start` com @userinfobot para confirmar seu ID |
| API retorna erro | Verifique logs: `log.error()` mostrará o erro exato |

## Testar conexão com API Telegram

```bash
# Verificar token válido
curl "https://api.telegram.org/bot{token}/getMe"
```

Se retornar JSON com dados do bot, o token está válido!
