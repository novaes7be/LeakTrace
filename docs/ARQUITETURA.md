# Arquitetura do LeakTrace

Fonte de verdade para decisões de estrutura. Mudanças relevantes geram ADR em [adr/](adr/README.md). Onde cada coisa fica no repositório: [MAPA.md](MAPA.md).

## Visão geral

```
             fontes                         propagação                     saídas
  @Sensitive (annotations)  ──┐
                              ├─► taint-engine ──► String/StringBuilder ──► sinks ──► TaintEvent ──► reporter
  Rule (rules), por padrão  ──┘   (mapa fraco      .concat/.append/...      (LOG,     (core)         (terminal,
                                   por identidade)                          HTTP,                    JSON, SARIF)
                                                                            EXCEPTION)
            agent: instrumenta tudo acima com ByteBuddy e liga as peças em runtime
```

Fluxo de dados: `annotations/rules -> taint-engine -> sinks -> TaintEvent (core) -> reporter`.

## Camadas e dependências

| Camada | Módulos | Pode depender de |
|--------|---------|------------------|
| Contratos | `leaktrace-annotations`, `leaktrace-core` | `annotations` (o core) e nada mais |
| Bibliotecas | `taint-engine`, `rules`, `sinks`, `reporter`, `processor` | só `core` e `annotations` |
| Montagem | `agent`, `spring-boot-starter`, `integration-tests`, `demo-app` | qualquer módulo |
| Distribuição | `bom`, `action` | nenhum módulo Java |

Verificado no build (maven-enforcer `enforce-module-boundaries`). Módulos de montagem desligam a regra no próprio pom. ByteBuddy é proibido fora do agente (exceto em escopo de teste, por causa do Mockito). Só o `reporter` escreve no terminal (Checkstyle `noConsoleOutput`).

## Contratos (core)

Mudança exige aprovação dos três devs.

| Contrato | Pacote | Papel |
|----------|--------|-------|
| `TaintEvent` | `core.event` | Record imutável de um vazamento. Só valor mascarado, sem referências a objetos da aplicação. |
| `Sink` | `core.sink` | Declara `InterceptionPoint`s e, por chamada, devolve `SinkOutcome` (`Proceed`, `Replace`, `Block`). |
| `Rule` | `core.rule` | Encontra ocorrências (`Match`: só posições) e mascara. Sem estado. |
| `Reporter` | `core.report` | Recebe eventos sem bloquear; `close()` no shutdown emite resumo/arquivo. |
| `TaintLookup` | `core.taint` | Visão somente-leitura do motor, entregue aos sinks. |
| `LeakTraceConfig` | `core.config` | Configuração imutável (regras, allowlist, só-alertar, severidade mínima). |

Correspondência entre ação e desfecho: `MASK` → `Replace`, `BLOCK` → `Block`, `ALERT` → `Proceed` com eventos. Com `alertOnly=true`, toda ação vira `ALERT`.

## Ciclo de vida em runtime

1. **premain** (`agent`): lê argumentos (`-javaagent:leaktrace-agent.jar=config=leaktrace.yml`), carrega `LeakTraceConfig` (YAML via Jackson relocado no agente; ausente → `LeakTraceConfig.defaults()`).
2. **Descoberta**: `ServiceLoader` encontra `Rule`, `Sink` e `Reporter` empacotados no jar do agente. Regras desligadas na configuração são descartadas.
3. **Bootstrap**: classes que o código instrumentado do JDK chama (motor de taint, ponte para sinks) vão para o classloader de bootstrap (`appendToBootstrapClassLoaderSearch`), ver ADR-002.
4. **Instrumentação**: o agente traduz cada `InterceptionPoint` em matcher ByteBuddy (`Advice` inline) e instrumenta:
   - **fontes**: leitura de campos, parâmetros, accessors e componentes de record com `@Sensitive` marca o valor no motor;
   - **propagação**: `String`, `StringBuilder`, `String.format` e afins copiam o taint para o resultado e registram `PropagationStep`;
   - **sinks**: chamadas declaradas pelos `Sink`s.
5. **Na chamada de sink**: o advice monta uma `SinkInvocation` (sem copiar argumentos), chama `Sink#inspect`, aplica o `SinkOutcome` (segue, substitui argumentos ou não executa) e entrega os eventos ao pipeline.
6. **Pipeline de eventos**: filtra por `minimumSeverity` e allowlist, aplica overrides de severidade e repassa a cada `Reporter`.
7. **Shutdown hook**: `Reporter#close()` em todos (resumo no terminal, arquivos JSON/SARIF).

## Rastreio de taint

Detalhes e alternativas em [ADR-001](adr/ADR-001-rastreio-por-identidade.md).

- Mapa de chaves fracas comparadas por identidade (`WeakIdentityMap`, implementação própria). Não usar `java.util.WeakHashMap`, que compara por `equals`.
- Não marcar strings internadas nem constantes de compilação.
- Segunda linha de defesa: nos sinks, as `Rule`s varrem o payload textual para pegar dados que perderam o taint (ex.: reconstruídos via `char[]`). Achados por regra têm `Origin.Kind.RULE`.

## Isolamento de falhas e desempenho

- Todo advice captura `Throwable`, incrementa um contador interno e segue com o comportamento original. O agente nunca derruba a aplicação.
- `Sink#inspect` e `Reporter#report` não podem lançar exceção nem bloquear; reporters lentos enfileiram.
- O caminho comum (argumento sem taint) precisa ser O(1): uma consulta ao mapa por argumento.
- Overhead medido com JMH e com o demo-app sob carga a partir da semana 3; meta de p99 em [PLANO.md](PLANO.md).

## Empacotamento

- `leaktrace-agent` gera um jar shaded com motor, regras, sinks e reporters. Dependências de terceiros (ByteBuddy, Jackson) relocadas para `io.github.novaes7be.leaktrace.shaded.*`.
- `leaktrace-spring-boot-starter` traduz propriedades `leaktrace.*` para `LeakTraceConfig` e documenta como anexar o agente; não reimplementa instrumentação.
- `leaktrace-processor` roda no build do usuário (annotation processing) e valida o uso de `@Sensitive`/`@Ignore`; não participa do runtime.
- `leaktrace-bom` alinha versões para consumidores; não tem parent, para não exportar o `dependencyManagement` de terceiros.

## Saídas

| Reporter | Quando | Conteúdo |
|----------|--------|----------|
| terminal | tempo real e shutdown | uma linha por achado; resumo agrupado por tipo e sink |
| JSON | shutdown | lista de `TaintEvent` serializados |
| SARIF 2.1.0 | shutdown | mapeamento em [ADR-003](adr/ADR-003-sarif.md); consumido pela `leaktrace-action` |

## Configuração

```yaml
leaktrace:
  alert-only: false          # true: nada é mascarado/bloqueado, só reportado
  minimum-severity: MEDIUM   # ausente: LOW
  rules:
    - id: br.cpf
      severity: CRITICAL
      action: BLOCK
  allowlist:
    - location: com.acme.audit.*
      reason: logger de auditoria já mascara
```

Precedência da ação: `alert-only` > `rules[].action` > `@Sensitive.action` > `MASK`. Ids de regra duplicados e listas nulas são rejeitados na construção do modelo.

## Estratégia de testes

| Nível | Onde | Como |
|-------|------|------|
| Unitário | cada módulo | JUnit 5 + fakes de `leaktrace-core:test-fixtures` (`FakeSink`, `FakeRule`, `FakeReporter`, `FakeTaintLookup`, `FakeSinkInvocation`, `TestData`) |
| Linha de base | `demo-app` | `PlantedLeaksTest` prova que os três vazamentos existem sem agente |
| Ponta a ponta | `integration-tests` | sobe o demo-app com `-javaagent` e prova que os vazamentos são mascarados/bloqueados/reportados |
| Desempenho | `agent` (JMH) | overhead de propagação e de sink |

Dados de teste são sempre sintéticos (`DemoData`, `TestData`).

## Build e qualidade

`./mvnw clean verify` executa: enforcer (versões de Java/Maven/plugins, convergência, fronteiras de módulo) → Checkstyle (inclui `src/testFixtures`) → compilação `--release 17` → testes → JaCoCo (80% linhas, 70% branches por módulo). CI em GitHub Actions com Java 17 e 21; proteção do `main` em [BRANCH_PROTECTION.md](BRANCH_PROTECTION.md).

O `demo-app` usa o BOM do Spring Boot 4.1.2 e JUnit 6 (exigido pelo Boot 4); os demais módulos usam JUnit 5.
