# DECISIONS — Histórico de decisões
Gerenciador de Consultas | Iniciado: 2026-06-03

---

## [2026-06-04] CI/CD Pipeline com GitHub Actions + Quality Gates

**O que mudou:** Configurado CI/CD automatizado com GitHub Actions que roda em cada push/PR. Pipeline inclui: Checkstyle, Maven Surefire, SpotBugs, JaCoCo (cobertura mínima 70%).

**Por que:** Para garantir qualidade de código desde Fase 2 (TDD), onde cada entrega crítica requer validação. CI/CD bloqueia PR com teste falhando ou cobertura baixa, forçando padrão de código antes de avanço.

**Alternativa descartada:** CI/CD manual (rodar testes antes de commit) — descartada porque é fácil esquecer e permite code smell passar. Automatizar reduz carga cognitiva.

**Impacto:** 
- Toda tarefa [HITL] da Fase 2 em diante passa por pipeline obrigatório
- Checkstyle garante formatação consistente (max 120 chars, sem tabs, naming conventions)
- SpotBugs detecta bugs comuns (null pointers, resource leaks)
- JaCoCo bloqueia PR com cobertura < 70% (domínio deve estar ≥ 80%)
- Relatórios de cobertura disponíveis em artifacts do GitHub Actions

**Como reverter:** Deletar `.github/workflows/maven.yml` e remover plugins do `pom.xml` — não recomendado, pipeline é leve e robusto.

---

## [2026-06-03] Adoção de TDD + Arquitetura Hexagonal

**O que mudou:** Metodologia definida como TDD com arquitetura Hexagonal (Ports & Adapters) para o backend Spring Boot.

**Por que:** TDD foi escolhido pela presença de integração externa frágil (webhook da Evolution API) e regras de negócio críticas de agendamento. Hexagonal foi escolhido para isolar o domínio das integrações externas, facilitando os testes unitários e permitindo trocar adapters sem afetar o domínio.

**Alternativa descartada:** SDD puro sem TDD — descartado porque a integração com WhatsApp tem comportamento imprevisível e as regras de conflito de horário precisam de cobertura de teste robusta.

**Impacto:** O domínio (`AgendamentoService`, `ClienteService`) deve ser escrito com testes antes da implementação. Os adapters (JPA, Evolution API) são implementados depois do domínio estar testado.

**Como reverter:** Remover a camada de portas e mover a lógica para os controllers diretamente — não recomendado após a Fase 2 estar concluída.

---

## [2026-06-03] Spring Boot como backend (em vez de Node.js)

**O que mudou:** Backend alterado de Node.js + Express para Spring Boot 3 (Java 21).

**Por que:** Preferência e familiaridade do time com o ecossistema Spring. A integração com WhatsApp foi adaptada para usar Evolution API (REST) em vez de Baileys (biblioteca Node.js).

**Alternativa descartada:** Node.js + Express + Baileys — descartado porque o Baileys só funciona em ambiente Node, incompatível com Spring Boot.

**Impacto:** Evolution API roda como serviço separado e se comunica com o Spring Boot via webhooks e REST. Requer dois serviços no Railway (Spring Boot + Evolution API).

**Como reverter:** Não aplicável — decisão estrutural tomada antes de qualquer implementação.

---

## [2026-06-03] Restrição de custo zero no MVP

**O que mudou:** Toda a stack deve operar em free tiers.

**Por que:** Fase inicial do projeto sem receita ou orçamento definido.

**Alternativa descartada:** VPS dedicada (DigitalOcean, Hetzner) — descartada pelo custo.

**Impacto:** Hospedagem no Railway (free tier tem limitação de horas/mês) e Vercel (free tier sem limite de requisições para projetos pessoais). Supabase free tier tem limite de 500MB de banco e 50MB de storage — suficiente para o MVP. Monitorar uso ao crescer.

**Como reverter:** Migrar para plano pago no Railway e Supabase quando o volume justificar.
