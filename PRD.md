# PRD — Gerenciador de Consultas
Versão: 1.0 | Data: 2026-06-03

---

## Problema

Comércios locais que prestam serviços com hora marcada (barbearias, salões, clínicas estéticas, pet shops, oficinas, etc.) gerenciam agendamentos de forma manual — pelo WhatsApp, anotações em papel ou planilhas. Isso gera esquecimentos, conflitos de horário e falta de visibilidade sobre a agenda e histórico de clientes.

## Usuários

| Perfil | Papel | Como interage |
|---|---|---|
| **Cliente** | Quer agendar um serviço | Envia mensagem pelo WhatsApp |
| **Prestador** | Dono ou funcionário do comércio | Acessa o painel web para ver e gerenciar agendamentos |

## MVP — O que resolve 70% do problema

> "O cliente manda mensagem no WhatsApp e o agendamento aparece automaticamente no painel do prestador."

Funcionalidades do MVP:
- Receber mensagem do WhatsApp e registrar a consulta automaticamente
- Painel web para o prestador visualizar a agenda do dia/semana
- Cadastro básico de clientes (nome, telefone, histórico de consultas)
- Confirmação automática de agendamento enviada ao cliente via WhatsApp

## Fora do escopo (MVP)

- Pagamento online (cliente paga presencialmente)
- App mobile para o prestador
- Multi-prestador / multi-funcionário por conta
- Relatórios e métricas avançadas (faturamento, frequência)
- Integração com outros canais (Instagram DM, email, SMS)
- Sistema de avaliação de serviços

## Restrições

- Stack 100% gratuita na fase inicial
- Hospedagem em free tiers (Vercel, Railway)
- WhatsApp via Evolution API (self-hosted, open source)
- Uma conta = um negócio (sem multi-tenant no MVP)

## Evolução prevista (pós-MVP)

- App mobile para o prestador (visualizar agenda e receber notificações)
- Cancelamento e reagendamento pelo próprio cliente via WhatsApp
- Relatórios básicos de atendimento
