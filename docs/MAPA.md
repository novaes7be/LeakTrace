# Mapa do repositório

Onde fica cada coisa. Arquitetura e regras em [ARQUITETURA.md](ARQUITETURA.md).

## Módulos

Pacote base: `io.github.novaes7be.leaktrace.<pacote>`.

| Diretório | Pacote | Dono | Responsabilidade | Depende de | Tipos principais |
|-----------|--------|------|------------------|------------|------------------|
| `leaktrace-bom/` | — | A | versões alinhadas para consumidores | nada (sem parent) | — |
| `leaktrace-annotations/` | `annotations` | A | marcar fontes e supressões | nada | `@Sensitive`, `@Ignore`, `SensitiveType`, `Action` |
| `leaktrace-core/` | `core` | A | contratos, eventos, configuração, fakes | annotations | `TaintEvent`, `Sink`, `Rule`, `Reporter`, `TaintLookup`, `LeakTraceConfig` |
| `leaktrace-taint-engine/` | `engine` | A | mapa fraco por identidade e propagação | core | (semana 2) `WeakIdentityMap`, `TaintEngine` |
| `leaktrace-agent/` | `agent` | A | premain, ByteBuddy, jar shaded | todos (montagem) | (semana 3) |
| `leaktrace-rules/` | `rules` | B | detectores e allowlist | core | (semana 2) |
| `leaktrace-processor/` | `processor` | B | validação de anotações no build | core | (semana 5) |
| `leaktrace-sinks/` | `sinks` | C | LOG, HTTP_RESPONSE, EXCEPTION | core | (semanas 3–4) |
| `leaktrace-reporter/` | `reporter` | C | terminal, JSON, SARIF | core | (semanas 2–5) |
| `leaktrace-spring-boot-starter/` | `spring` | C | auto-configuração Spring Boot | montagem | (semana 5) |
| `leaktrace-action/` | — | C | GitHub Action (SARIF + comentário no PR) | nenhum módulo Java | (semana 6) |
| `integration-tests/` | `it` | B | ponta a ponta com agente | montagem | (semana 3+) |
| `demo-app/` | `demo` | C | Spring Boot 4 com 3 vazamentos plantados | annotations | `DemoData`, `PlantedLeaksTest` |

Cada módulo tem um `README.md` com responsabilidade, dono, API pública e dependências.

## Dentro do core

```
leaktrace-core/src/
├── main/java/io/github/novaes7be/leaktrace/core/
│   ├── event/    TaintEvent, Severity, CodeLocation, Origin, PropagationStep, SinkRef   [contrato]
│   ├── sink/     Sink, SinkType, InterceptionPoint, SinkInvocation, SinkOutcome         [contrato]
│   ├── rule/     Rule, Match                                                           [contrato]
│   ├── report/   Reporter                                                              [contrato]
│   ├── taint/    TaintLookup, TaintInfo
│   └── config/   LeakTraceConfig, RuleConfig, AllowlistEntry
├── testFixtures/java/.../core/testing/   fakes, publicados com classifier test-fixtures
└── test/java/                            testes do core e dos fakes
```

Usar os fakes em outro módulo:

```xml
<dependency>
    <groupId>io.github.novaes7be</groupId>
    <artifactId>leaktrace-core</artifactId>
    <classifier>test-fixtures</classifier>
    <scope>test</scope>
</dependency>
```

## Arquivos de raiz e infraestrutura

| Caminho | O que é |
|---------|---------|
| `pom.xml` | parent: versões, plugins, enforcer, Checkstyle, JaCoCo |
| `mvnw`, `.mvn/` | Maven wrapper (Maven 3.10.0) |
| `config/checkstyle/` | regras de estilo e supressões |
| `.github/workflows/ci.yml` | build e testes em PR, Java 17 e 21 |
| `.github/CODEOWNERS` | donos por módulo |
| `.github/pull_request_template.md`, `.github/ISSUE_TEMPLATE/` | templates |
| `CLAUDE.md` | instruções para o Claude Code |
| `CONTRIBUTING.md`, `SECURITY.md`, `LICENSE` | contribuição, segurança, Apache-2.0 |

## Documentos

| Documento | Conteúdo |
|-----------|----------|
| [PLANO.md](PLANO.md) | objetivo, cronograma de 6 semanas, definição de pronto, riscos |
| [ARQUITETURA.md](ARQUITETURA.md) | camadas, contratos, ciclo de vida, empacotamento, testes |
| [BACKLOG.md](BACKLOG.md) | tarefas por dev, semanas 2 a 6, com critérios de aceite |
| [BRANCH_PROTECTION.md](BRANCH_PROTECTION.md) | regras do `main` e comando para aplicar |
| [adr/](adr/README.md) | decisões de arquitetura |
