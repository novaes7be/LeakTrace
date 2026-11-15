# Backlog: semanas 2 a 6

Cada tarefa vira um PR. Id: `<dev><semana>.<n>`. Critérios de aceite são verificados por teste automatizado, salvo indicação. Toda tarefa também cumpre a definição de pronto do [PLANO.md](PLANO.md). Dependências entre tarefas aparecem em "Depende de".

Regra para todas: dados de teste sintéticos; nada de valor real em saída; o agente nunca derruba a aplicação.

---

## Semana 2: peças isoladas

Marco: motor, regras e reporter de terminal funcionando em testes unitários, só com os fakes do core.

### A2.1 `WeakIdentityMap` (taint-engine)
- [ ] Chave comparada por `==` e `System.identityHashCode`; duas `String` com mesmo conteúdo são chaves distintas.
- [ ] Entrada some depois que a chave é coletada (teste com `System.gc()` + `ReferenceQueue`, tolerante a timing).
- [ ] Seguro para acesso concorrente (teste com N threads fazendo put/get sem perda nem exceção).
- [ ] Sem dependências externas.

### A2.2 `TaintEngine` (taint-engine). Depende de A2.1
- [ ] Implementa `TaintLookup`.
- [ ] `mark(Object, TaintInfo)`, `propagate(Object from, Object to, PropagationStep)` acrescenta o passo ao caminho.
- [ ] Não marca `null`, strings internadas (`s == s.intern()`) nem tipos primitivos em caixa.
- [ ] `lookup` nunca lança exceção.

### A2.3 Publicar `leaktrace-bom` com todos os artefatos (bom)
- [ ] BOM lista todos os módulos publicáveis; teste de build (projeto de exemplo em `src/it` ou verificação de pom) resolve versões só pelo BOM.

### B2.1 Regras de documento BR (rules)
- [ ] `br.cpf`: aceita com e sem pontuação; valida dígitos verificadores; rejeita sequências repetidas (`000.000.000-00`).
- [ ] `br.cnpj`: idem, com dígitos verificadores.
- [ ] `mask` mantém só os 2 últimos dígitos.
- [ ] Teste de propriedade/tabela com ≥ 20 casos válidos e inválidos sintéticos.

### B2.2 Regras de cartão, e-mail e segredo (rules)
- [ ] `card.pan`: 13–19 dígitos, Luhn obrigatório, aceita espaços/hífens.
- [ ] `email`, `token.jwt` (três segmentos base64url com header JSON válido), `token.bearer`, `secret.api-key` (prefixos conhecidos configuráveis).
- [ ] Nenhum regex com backtracking catastrófico: teste com entrada de 100 KB adversarial termina em < 50 ms.

### B2.3 Allowlist (rules)
- [ ] `AllowlistMatcher` casa `AllowlistEntry` por glob em `classe#metodo` e, opcionalmente, por `ruleId`.
- [ ] Testes com `TestData`/`FakeRule`.

### B2.4 `ServiceLoader` das regras (rules)
- [ ] `META-INF/services/io.github.novaes7be.leaktrace.core.rule.Rule` registra todas as regras; teste carrega via `ServiceLoader` e confere os ids.

### C2.1 Reporter de terminal (reporter)
- [ ] Uma linha por evento em tempo real: severidade, tipo, valor mascarado, sink e call site.
- [ ] `close()` imprime resumo agrupado por tipo e sink; idempotente.
- [ ] `report` não bloqueia: fila limitada + thread daemon; eventos excedentes são contados e mostrados no resumo.
- [ ] Teste garante que nenhum caractere do `FakeRule.DEFAULT_MARKER` aparece na saída.

### C2.2 `SinkSupport` (sinks)
- [ ] Helper que, dada uma `SinkInvocation`, consulta `TaintLookup` em cada argumento e monta `TaintEvent`s com valor mascarado.
- [ ] Converte a ação em `SinkOutcome` (`MASK` → `Replace`, `BLOCK` → `Block`, `ALERT` → `Proceed`).
- [ ] Erro interno → `SinkOutcome.clean()` (teste com `TaintLookup` que lança).

---

## Semana 3: agente e primeiros sinks

Marco: o demo-app sobe com o agente sem quebrar; fontes `@Sensitive` são marcadas e propagadas.

### A3.1 Esqueleto do agente (agent)
- [ ] `premain` e `agentmain`; argumento `config=<caminho>`.
- [ ] Jar shaded com ByteBuddy e Jackson relocados para `io.github.novaes7be.leaktrace.shaded.*`; teste confere que nenhuma classe fora de `io/github/novaes7be/` existe no jar.
- [ ] Carrega `LeakTraceConfig` de YAML; arquivo ausente → `defaults()`; YAML inválido → aviso e `defaults()`, sem derrubar.
- [ ] Shutdown hook chama `Reporter#close()`.

### A3.2 Bootstrap e propagação (agent). Depende de A2.2, A3.1
- [ ] Classes do motor injetadas no bootstrap.
- [ ] Advice em `String.concat`, `StringBuilder.append/toString`, `String.valueOf`, `String.format`, `substring`.
- [ ] Teste: valor marcado → concatenado → `lookup` do resultado traz o `PropagationStep`.
- [ ] Exceção dentro do advice não chega à aplicação (teste com motor que lança).

### A3.3 Marcação de fontes `@Sensitive` (agent). Depende de A3.2
- [ ] Leitura de campo, accessor de record, getter e parâmetro anotados marca o valor com tipo e ação da anotação.
- [ ] `@Ignore` impede a marcação.
- [ ] Demo-app sobe com o agente e `PlantedLeaksTest` continua verde (agente ainda não age nos sinks).

### B3.1 Harness de integração (integration-tests). Depende de A3.1
- [ ] Sobe o `demo-app` em processo separado com `-javaagent` do jar recém-construído (failsafe).
- [ ] Captura stdout/stderr e relatórios; utilitário para fazer as 3 requisições do demo.
- [ ] Roda no CI em Java 17 e 21.

### B3.2 Processor: validações básicas (processor)
- [ ] Erro de compilação: `@Sensitive` em tipo que não é `CharSequence`, `char[]`, `byte[]` ou wrapper de dado.
- [ ] Aviso: `@Sensitive` e `@Ignore` no mesmo elemento.
- [ ] Testes com `javax.tools.JavaCompiler` em memória.

### C3.1 Sink LOG (sinks). Depende de C2.2
- [ ] `InterceptionPoint`s para SLF4J `Logger` (todos os níveis, com e sem argumentos), Logback `Appender`, `System.out/err` `PrintStream.print*`.
- [ ] Argumentos de formato (`{}`) e mensagem final são inspecionados.
- [ ] Testes unitários com `FakeSinkInvocation`/`FakeTaintLookup`.

### C3.2 Sink EXCEPTION (sinks). Depende de C2.2
- [ ] Intercepta construtores de `Throwable` com mensagem e `getMessage`.
- [ ] `MASK` substitui a mensagem; `BLOCK` troca por mensagem genérica (não impede a exceção em si).

---

## Semana 4: ponta a ponta

Marco: vazamentos #1 (CPF no log) e #3 (senha na exceção) detectados e mascarados no demo-app com o agente.

### A4.1 Ligar sinks ao agente (agent). Depende de A3.2, C3.1
- [ ] Cada `InterceptionPoint` vira matcher ByteBuddy; `SinkInvocation` sem cópia de argumentos.
- [ ] Aplica `SinkOutcome`: `Replace` troca argumentos, `Block` não executa o método (retorno padrão do tipo), `Proceed` segue.
- [ ] Pipeline de eventos aplica `minimumSeverity`, allowlist, overrides de severidade e `alertOnly`.

### A4.2 Contador de falhas internas (agent)
- [ ] Toda falha capturada em advice incrementa contador por ponto; resumo mostra os contadores (via reporter).

### B4.1 Integração: vazamentos #1 e #3 (integration-tests). Depende de B3.1, A4.1, C3.1, C3.2
- [ ] Com agente: o CPF real não aparece no stdout; aparece a versão mascarada e um evento LOG.
- [ ] Com agente: a senha errada não aparece no corpo do 401; evento EXCEPTION reportado.
- [ ] Com `alert-only: true`: valores passam e eventos são reportados.

### B4.2 Regras como segunda linha nos sinks (rules + sinks, em par com C). Depende de B2.4, C2.2
- [ ] **Decisão de contrato pendente (aprovação dos 3 devs + ADR):** hoje nem `Sink` nem `SinkInvocation` dão acesso às `Rule`s ativas. Opções: `SinkInvocation#rules()`, ou um `SinkContext` entregue na inicialização do sink. Decidir até o fim da semana 3, antes de C3.1 fechar.
- [ ] Sinks varrem o payload textual com as `Rule`s ativas; achado gera evento com `Origin.Kind.RULE`.
- [ ] Teste: CPF construído via `char[]` (sem taint) ainda é detectado no log.

### C4.1 Sink HTTP_RESPONSE (sinks). Depende de C2.2
- [ ] Intercepta escrita no corpo (Servlet `ServletOutputStream`/`PrintWriter`) e serialização Spring (`HttpMessageConverter#write`).
- [ ] Headers de resposta inspecionados.

### C4.2 Reporter JSON (reporter)
- [ ] Grava `leaktrace-report.json` no shutdown (caminho configurável) com todos os eventos.
- [ ] Teste de round-trip: JSON lido de volta reconstrói os mesmos `TaintEvent`.

---

## Semana 5: integração e saída para CI

Marco: os três vazamentos detectados; SARIF válido; starter funcionando; overhead medido.

### A5.1 Benchmark de overhead (agent)
- [ ] JMH para propagação (`concat`, `append`) e sink LOG, com e sem agente.
- [ ] Carga no demo-app (ex.: 10k requisições) com p50/p99 com e sem agente, registrada em `docs/PERFORMANCE.md`.
- [ ] Meta do PLANO atingida ou ADR explicando o desvio e o plano.

### A5.2 Preparar publicação (bom, agent, raiz)
- [ ] `sources`, `javadoc` e assinatura GPG em perfil `release`; `central-publishing-maven-plugin` configurado.
- [ ] Namespace `io.github.novaes7be` verificado no Central Portal (manual, registrar no PR).
- [ ] Dry-run de deploy sem erros.

### B5.1 Integração: vazamento #2 e allowlist (integration-tests). Depende de C4.1
- [ ] Com agente: token interno não aparece no JSON de `/api/me`; evento HTTP_RESPONSE reportado.
- [ ] Entrada de allowlist para o call site suprime o evento.
- [ ] `@Ignore` em um campo do demo suprime o evento.

### B5.2 Processor completo (processor)
- [ ] Processor registrado em `META-INF/services`; demo-app compila com ele ativo.
- [ ] Aviso quando classe com `@Sensitive` sobrescreve `toString` expondo o campo (heurística documentada).

### C5.1 Reporter SARIF (reporter). Depende de C4.2
- [ ] Gera SARIF 2.1.0 conforme mapeamento do ADR-003, com `codeFlows` e `partialFingerprints`.
- [ ] Validado contra o JSON Schema oficial em teste.
- [ ] Nenhum `snippet`; mensagem só com valor mascarado.

### C5.2 Spring Boot starter (spring-boot-starter)
- [ ] Propriedades `leaktrace.*` mapeadas para `LeakTraceConfig` (mesmos nomes do YAML), com metadata para autocompletar.
- [ ] Teste com `ApplicationContextRunner`.
- [ ] README explica como anexar o agente em apps Spring Boot.

---

## Semana 6: release

Marco: 0.1.0 publicado no Maven Central; Action usável em outro repositório.

### A6.1 Robustez multi-JDK (agent)
- [ ] CI com Java 17, 21 e 25 para agent e integration-tests.
- [ ] Teste de aplicação com ByteBuddy próprio em outra versão (sem conflito graças à relocação).

### A6.2 Release 0.1.0 (raiz)
- [ ] Tag `v0.1.0`, artefatos no Central, notas de release.
- [ ] `leaktrace-bom` importável por um projeto externo de exemplo.

### B6.1 Suíte de regressão (integration-tests)
- [ ] Matriz de cenários (3 vazamentos × MASK/BLOCK/ALERT × alert-only) roda no CI.
- [ ] Teste de que o agente não altera respostas sem dado sensível (comparação byte a byte com e sem agente).

### B6.2 Catálogo de regras (rules, docs)
- [ ] Tabela de regras com id, tipo, severidade padrão, exemplos sintéticos e formato da máscara em `leaktrace-rules/README.md`.

### C6.1 GitHub Action (action). Depende de C5.1
- [ ] `action.yml` (composite) que publica o SARIF via `github/codeql-action/upload-sarif` e comenta o resumo no PR.
- [ ] Workflow de exemplo neste repositório usando a Action no demo-app.

### C6.2 Documentação de uso (docs, README)
- [ ] README raiz com quickstart (agente em app qualquer e via starter), configuração, saídas e limitações.
- [ ] `demo-app/README.md` com o passo a passo antes/depois do agente.
- [ ] PLANO, ARQUITETURA, MAPA e CLAUDE.md revisados e consistentes com o código.
