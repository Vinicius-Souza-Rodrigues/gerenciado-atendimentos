# CI/CD Pipeline — Gerenciador de Atendimentos

**Versão:** 1.0 | **Data:** 2026-06-04

---

## O que foi configurado

### 1. **GitHub Actions Workflow** (`.github/workflows/maven.yml`)

Roda automaticamente em:
- `push` para `main` ou `develop`
- `pull_request` para `main` ou `develop`

**Etapas:**
1. ✅ **Checkstyle** — Validação de formatação de código
2. ✅ **Unit Tests** — Execução de testes com Maven Surefire
3. ✅ **SpotBugs** — Detecção de bugs comuns
4. ✅ **JaCoCo** — Cobertura de código (mínimo 70%)
5. ✅ **Build** — Compilação do JAR (sem testes, apenas validação)

**Resultado:** bloqueia PR se qualquer etapa falhar.

---

## Como rodar localmente

### Pré-requisitos
- Java 21 instalado
- Maven (incluído: `mvnw` ou `mvnw.cmd`)

### Executar toda a validação (como CI/CD faz)

```bash
cd backend

# Validar formatação
./mvnw checkstyle:check

# Rodar testes
./mvnw clean test

# Validar bugs
./mvnw spotbugs:check

# Gerar relatório de cobertura
./mvnw jacoco:report
```

**Ver resultado de cobertura:**
```bash
# Windows
start target\site\jacoco\index.html

# Mac/Linux
open target/site/jacoco/index.html
```

### Ou em um comando só

```bash
./mvnw clean test checkstyle:check spotbugs:check jacoco:report
```

---

## Convenções de código

### **Checkstyle rules:**
- Linhas: máx 120 caracteres
- Arquivos: máx 1500 linhas
- Sem tabs (use espaços)
- Nomes de classes: `PascalCase` (ex: `AgendamentoService`)
- Nomes de métodos/variáveis: `camelCase` (ex: `criarAgendamento`)
- Constantes: `SNAKE_CASE_UPPER` (ex: `MAX_RETRIES`)
- Complexidade ciclomática: máx 10

### **SpotBugs (Bug Detection):**
- Detecta padrões comuns de bugs (null pointers, resource leaks, etc)
- Falsos positivos estão filtrados em `.spotbugsExcludeFilter.xml`
- Se novo falso positivo for detectado: adicione à whitelist

### **JaCoCo (Code Coverage):**
- Mínimo global: 70%
- Domínio: idealmente 80%+ (regras de negócio críticas)
- Teste: ignorado na cobertura

---

## Estrutura dos testes

### Padrão de nomenclatura

```java
// Classe de teste
src/test/java/com/agendamentos/domain/AgendamentoServiceTest.java

// Método de teste (Given-When-Then)
void givenHorarioDisponivel_whenCriarAgendamento_thenRetornaSucesso()
```

### Exemplo de teste TDD

```java
@Test
void givenHorarioDisponivel_whenCriarAgendamento_thenRetornaSucesso() {
    // Given: um horário disponível
    LocalDateTime horario = LocalDateTime.now().plusHours(1);
    Agendamento agendamento = Agendamento.criar(
        clienteId,
        prestadorId,
        horario,
        "Corte"
    );

    // When: criar agendamento
    var resultado = agendamentoService.criar(agendamento);

    // Then: deve suceder
    assertThat(resultado).isSuccess();
    assertThat(resultado.getValue().getStatus()).isEqualTo(StatusAgendamento.CONFIRMADO);
}
```

---

## Validação antes de commitar

**Checklist antes de fazer commit:**

- [ ] Rodei `mvnw clean test` — todos passam
- [ ] Rodei `mvnw checkstyle:check` — sem erros
- [ ] Rodei `mvnw spotbugs:check` — sem bugs
- [ ] Rodei `mvnw jacoco:report` — cobertura ≥ 70%
- [ ] Código segue convenções (nomes, linhas, indentação)
- [ ] Testes cobrem caminho feliz + exceções

---

## Exceções ao CI/CD

Se CI/CD falhar legitimamente e você precisar ajustar:

1. **Checkstyle falha:** Ajuste `checkstyle.xml` e explique no commit
2. **SpotBugs falsa positiva:** Adicione à `.spotbugsExcludeFilter.xml` com comentário
3. **Cobertura baixa:** Escreva testes para as linhas não cobertas
4. **Nova dependência:** Adicione ao `pom.xml` e rode `mvnw dependency:tree` para validar

**Nunca:** desabilite uma ferramenta só porque está incomodando. Ajuste o código.

---

## Status do projeto

✅ Fase 1 — Setup/Infraestrutura (concluído)
⏳ Fase 2 — Domínio core (TDD) [HITL]
  - CI/CD agora valida todas as entregas
  - Todas as tasks requerem validação antes de merge

---

## Referências

- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [Checkstyle Documentation](https://checkstyle.org/)
- [SpotBugs](https://spotbugs.readthedocs.io/)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/trunk/doc/index.html)
- [GitHub Actions for Java](https://docs.github.com/en/actions/guides/building-and-testing-java-with-maven)
