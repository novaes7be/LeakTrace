# LeakTrace

Java agent de taint tracking em runtime. Detecta dados sensíveis (CPF, cartão, token, senha) indo parar em logs, respostas HTTP e exceptions. Reporta no terminal (tempo real + resumo no shutdown), JSON e SARIF. Publicado no Maven Central (io.github.novaes7be).

## Mapa
leaktrace/
├── leaktrace-bom/            versões alinhadas            [A]
├── leaktrace-annotations/    @Sensitive, @Ignore          [A]
├── leaktrace-core/           contratos, eventos, config   [A]
├── leaktrace-taint-engine/   rastreio por identidade      [A]
├── leaktrace-agent/          premain, ByteBuddy, shaded   [A]
├── leaktrace-rules/          detectores, allowlist        [B]
├── leaktrace-processor/      annotation processor (build) [B]
├── leaktrace-sinks/          log, HTTP, exceptions        [C]
├── leaktrace-reporter/       terminal, resumo, SARIF      [C]
├── leaktrace-spring-boot-starter/                         [C]
├── leaktrace-action/         GitHub Action (comentário PR)[C]
├── demo-app/                 Spring Boot com vazamentos   [C]
├── integration-tests/        ponta a ponta                [B]
└── docs/                     PLANO, ARQUITETURA, MAPA, ADRs, BACKLOG

Detalhe por módulo (pacote, tipos, dependências): docs/MAPA.md.

## Fluxo de dados
annotations/rules -> taint-engine -> sinks -> TaintEvent (core) -> reporter

## Contratos (core): não alterar sem aprovação dos 3 devs
TaintEvent, Sink, Rule, Reporter (pacotes core.event, core.sink, core.rule, core.report).
Também em core: TaintLookup (visão do motor para sinks) e LeakTraceConfig.

## Regras de dependência (verificadas no build)
- Módulo de biblioteca depende apenas de core e annotations (maven-enforcer).
- Nunca um módulo depender de outro módulo de implementação.
- Isentos por serem de montagem: agent, spring-boot-starter, integration-tests, demo-app.
- agent é o único que usa ByteBuddy (enforcer); reporter é o único que escreve no terminal (Checkstyle).

## Convenções
- Java 17 (`--release 17`), Maven, JUnit 5 + Mockito. Todo código novo com teste.
- demo-app usa Spring Boot 4.1.2 e JUnit 6 (exigido pelo Boot 4); só ele.
- Commits convencionais; um PR por tarefa; 1 aprovação; CI verde (Java 17 e 21).
- Revisão cruzada: A revisa B, B revisa C, C revisa A.
- Pacote base: io.github.novaes7be.leaktrace.<modulo>. Exceções (hífen não é válido): taint-engine → `engine`, spring-boot-starter → `spring`, integration-tests → `it`, demo-app → `demo`.
- Cobertura mínima por módulo: 80% linhas, 70% branches (JaCoCo). Baixar exige justificativa no pom.
- Estilo: config/checkstyle; 4 espaços, 120 colunas.

## Comandos
- Build completo: ./mvnw -q clean verify
- Um módulo: ./mvnw -q -pl leaktrace-<modulo> -am verify
- Demo sem agente: ./mvnw -pl demo-app -am spring-boot:run
- Demo com agente: ver demo-app/README.md (a partir da semana 3)

## Regras para o Claude Code
- Antes de editar, identifique o módulo e seu dono; mexa só no módulo da tarefa.
- Use os fakes de leaktrace-core (src/testFixtures, classifier `test-fixtures`) para testar sem depender de outros módulos.
- Mudança em contrato: pare e pergunte.
- Nunca imprimir, logar ou persistir o valor real de dado sensível; sempre mascarado. Dados de teste só sintéticos (DemoData, TestData).
- Agente deve ter overhead baixo e nunca derrubar a aplicação (capturar e isolar exceções).
- Siga docs/ARQUITETURA.md e docs/BACKLOG.md como fonte de verdade; avise se algo conflitar.
- Decisão de arquitetura nova: registrar em docs/adr/.
- Respostas curtas; explique só se eu pedir.
