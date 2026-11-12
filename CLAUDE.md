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

## Fluxo de dados
annotations/rules -> taint-engine -> sinks -> TaintEvent (core) -> reporter

## Contratos (core): não alterar sem aprovação dos 3 devs
TaintEvent, Sink, Rule, Reporter

## Regras de dependência
- Todo módulo depende apenas de core e annotations.
- Nunca um módulo depender de outro módulo de implementação.
- agent é o único que usa ByteBuddy; reporter é o único que escreve no terminal.

## Convenções
- Java 17, Maven, JUnit 5 + Mockito. Todo código novo com teste.
- Commits convencionais; um PR por tarefa; 1 aprovação; CI verde.
- Revisão cruzada: A revisa B, B revisa C, C revisa A.
- Pacote base: io.github.novaes7be.leaktrace.<modulo>

## Comandos
- Build completo: mvn -q clean verify
- Um módulo: mvn -q -pl leaktrace-<modulo> -am verify
- Demo com agente: ver demo-app/README.md

## Regras para o Claude Code
- Antes de editar, identifique o módulo e seu dono; mexa só no módulo da tarefa.
- Use os fakes de core/testFixtures para testar sem depender de outros módulos.
- Mudança em contrato: pare e pergunte.
- Nunca imprimir, logar ou persistir o valor real de dado sensível; sempre mascarado.
- Agente deve ter overhead baixo e nunca derrubar a aplicação (capturar e isolar exceções).
- Siga docs/ARQUITETURA.md e docs/BACKLOG.md como fonte de verdade; avise se algo conflitar.
- Respostas curtas; explique só se eu pedir.
