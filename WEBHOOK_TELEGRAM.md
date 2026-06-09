# Webhook Telegram - Integração Bidirecional

## O que foi implementado

Sistema completo para o cliente interagir com o bot Telegram para:
- ✅ Marcar agendamentos
- ✅ Ver horários disponíveis
- ✅ Avisar chegada
- ✅ Cancelar agendamentos
- ✅ Mensagens personalizadas pelo dono da empresa

---

## Arquitetura

### 1. **Fluxo de Recebimento**
```
Cliente: "Quero marcar corte em 10/06 às 14h"
    ↓
Telegram API → POST /webhook/telegram
    ↓
TelegramWebhookController (recebe a mensagem)
```

### 2. **Processamento de Mensagem**
```
TelegramWebhookController
    ↓
AgendamentoMessageParser (identifica comando)
    ↓
Tipos de comando:
  - MARCAR_AGENDAMENTO
  - LISTAR_HORARIOS
  - AVISAR_CHEGADA
  - CANCELAR_AGENDAMENTO
  - DESCONHECIDO
```

### 3. **Resposta Personalizada**
```
MensagensPersonalizadasService
    ↓
Gera mensagem HTML formatada
    ↓
TelegramService → Telegram API → Cliente
```

---

## Componentes Implementados

### **TelegramMensagemRequest.java**
DTO que mapeia a estrutura do webhook do Telegram:
```java
{
  "update_id": 123,
  "message": {
    "message_id": 456,
    "chat": {
      "id": "1250468667",
      "first_name": "João"
    },
    "text": "Quero marcar corte em 10/06 às 14h"
  }
}
```

### **AgendamentoMessageParser.java**
Parser inteligente que:
- Identifica tipo de comando via regex
- Extrai dados (serviço, data, hora)
- Faz parse de data/hora
- Trata variações de linguagem natural

Exemplos reconhecidos:
- "Quero marcar corte em 10/06 às 14h"
- "Gostaria de agendar barba para 15/06 às 10h"
- "Que horários tem disponível?"
- "Cheguei!"
- "Cancelar meu agendamento"

### **MensagensPersonalizadasService.java**
Templates de mensagens HTML:
- `mensagemBoasVindas()` - boas-vindas com instruções
- `mensagemAgendamentoConfirmado()` - confirmação com detalhes
- `mensagemHorariosDisponiveis()` - lista de horários
- `mensagemErroAgendamento()` - erro com motivo
- `mensagemCancelamento()` - confirmação de cancelamento
- `mensagemLembrete()` - lembrete de consulta
- `mensagemClienteChegou()` - notifica prestador que cliente chegou

### **TelegramWebhookController.java**
Endpoint POST `/webhook/telegram` que:
1. Recebe mensagem do webhook
2. Identifica tipo de comando
3. Processa a requisição
4. Responde de volta ao cliente

### **Atualizações na entidade Prestador**
Novos campos:
- `endereco` - endereço do negócio
- `mensagemPersonalizada` - mensagem customizável pelo dono

---

## Como Configurar o Webhook

### Passo 1: Registrar o webhook no Telegram

**Importante:** O endpoint agora requer o ID do prestador na URL, pois um bot pode atender múltiplos prestadores.

```bash
curl "https://api.telegram.org/bot{TOKEN}/setWebhook?url=https://seu-dominio.com/webhook/telegram/{PRESTADOR_ID}"
```

Onde:
- `{TOKEN}` = seu bot token (8842093458:AAHQegGrRpspGPVcFhezC-13syyqJPlgCz4)
- `{PRESTADOR_ID}` = UUID do prestador (ex: 550e8400-e29b-41d4-a716-446655440000)
- `https://seu-dominio.com` = URL pública do seu servidor

**Exemplo completo:**
```bash
curl "https://api.telegram.org/bot8842093458:AAHQegGrRpspGPVcFhezC-13syyqJPlgCz4/setWebhook?url=https://seu-dominio.com/webhook/telegram/550e8400-e29b-41d4-a716-446655440000"
```

### Passo 2: Para desenvolvimento local com ngrok

```bash
# 1. Instale ngrok: https://ngrok.com/download
# 2. Rode seu app na porta 8080
# 3. Em outro terminal:
ngrok http 8080
# 4. Você receberá uma URL pública: https://xyz-abc.ngrok.io
# 5. Registre o webhook:
curl "https://api.telegram.org/bot{TOKEN}/setWebhook?url=https://xyz-abc.ngrok.io/webhook/telegram"
```

### Passo 3: Verificar webhook ativo

```bash
curl "https://api.telegram.org/bot{TOKEN}/getWebhookInfo"
```

---

## Fluxo de Exemplo Completo

### Cliente quer marcar agendamento

```
Cliente manda: "Quero marcar barba em 15/06 às 10h"
    ↓
Bot identifica: MARCAR_AGENDAMENTO
    ↓
Parser extrai: 
  - Serviço: "barba"
  - Data: "15/06"
  - Hora: "10h"
    ↓
Sistema valida data/hora
    ↓
Bot responde:
  "✅ Agendamento recebido!
   Serviço: barba
   Data/Hora: 15/06 às 10:00
   
   Em breve você receberá a confirmação!"
```

### Cliente quer ver horários

```
Cliente manda: "Que horários tem disponível?"
    ↓
Bot identifica: LISTAR_HORARIOS
    ↓
Bot responde:
  "📅 Horários disponíveis para Geral:
   
   ✅ 09:00
   ✅ 10:30
   ✅ 14:00
   ✅ 15:30"
```

### Cliente chega na consulta

```
Cliente manda: "Cheguei!"
    ↓
Bot identifica: AVISAR_CHEGADA
    ↓
Bot responde:
  "✅ Recebido!
   
   O prestador será avisado de sua chegada.
   Bem-vindo! 👋"
    ↓
Sistema avisa prestador no Telegram/WhatsApp
```

---

## Customizações pelo Dono da Empresa

O dono pode customizar:

1. **Mensagem de boas-vindas**: Saudação e instruções personalizadas
2. **Nome do negócio**: Aparece nas mensagens
3. **Endereço**: Incluído nas confirmações
4. **Mensagem personalizada**: Campo livre para adicionar informações

Exemplo:
```json
{
  "nomeNegocio": "Barbearia Premium",
  "endereco": "Rua das Flores, 123 - Centro",
  "mensagemPersonalizada": "⏰ Horário de funcionamento: 09h às 18h\n📞 Dúvidas: (11) 99999-9999"
}
```

Resultado na mensagem de boas-vindas:
```
Bem-vindo ao agendamento de Barbearia Premium! 👋

Aqui você pode:
✅ Marcar uma consulta
✅ Ver horários disponíveis
✅ Cancelar um agendamento

Como usar:
• "Quero marcar corte em 10/06 às 14h"
• "Que horários tem disponível?"
• "Cheguei!" (quando estiver chegando)

⏰ Horário de funcionamento: 09h às 18h
📞 Dúvidas: (11) 99999-9999
```

---

## Fluxo Completo (com persistência)

```
1. Cliente: "Quero marcar corte em 10/06 às 14h"
   ↓
2. Webhook recebe: POST /webhook/telegram/{prestadorId}
   ↓
3. Parser identifica: MARCAR_AGENDAMENTO
   Extrai: serviço=corte, data=10/06, hora=14h
   ↓
4. Controller busca/cria Cliente pelo telegram_chat_id
   ↓
5. Chama AgendamentoService.criar(clienteId, prestadorId, dataHora, servico)
   ↓
6. Service valida: data/hora no futuro, sem conflito
   ↓
7. Salva Agendamento no banco (CONFIRMADO)
   ↓
8. MensagensPersonalizadasService gera confirmação com dados reais
   ↓
9. TelegramService envia resposta ao cliente com detalhes do agendamento
   ↓
10. Cliente recebe: "✅ Agendamento confirmado! Corte em 10/06 às 14:00"
```

## Próximos Passos

1. ✅ Parser de mensagem
2. ✅ Webhook controller
3. ✅ Mensagens personalizadas
4. ✅ **Integração com banco** - salvar os agendamentos recebidos pelo bot
5. ⏳ **Horários dinâmicos** - listar baseado em conflitos reais
6. ⏳ **Lembretes automáticos** - enviar mensagem 1h antes
7. ⏳ **Dashboard do dono** - gerenciar mensagens customizadas
8. ⏳ **WhatsApp webhook** - mesmo fluxo para WhatsApp

---

## Testes

Para testar localmente sem ngrok, você precisa de um prestadorId válido. Primeiro, crie um prestador via:

```bash
POST /api/prestadores HTTP/1.1
Content-Type: application/json

{
  "nomeNegocio": "Barbearia Premium",
  "telefoneWhatsApp": "11999999999",
  "endereco": "Rua das Flores, 123",
  "mensagemPersonalizada": "Bem-vindo! ⏰ Funcionamos de 09h às 18h"
}
```

Depois, use o ID retornado no webhook:

```bash
curl -X POST http://localhost:8080/webhook/telegram/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "update_id": 123,
    "message": {
      "message_id": 456,
      "chat": { "id": "1250468667", "first_name": "João" },
      "text": "Quero marcar corte em 10/06 às 14h"
    }
  }'
```

**Resposta esperada:**
- Status: 200 OK
- No banco: novo Cliente criado com telegram_chat_id = "1250468667"
- No banco: novo Agendamento criado com status CONFIRMADO
- Cliente recebe no Telegram: confirmação com data, horário, endereço
