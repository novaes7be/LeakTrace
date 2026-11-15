# Contribuindo com o LeakTrace

## Ambiente

- Java 17+ (CI roda 17 e 21) e Git. Maven vem pelo wrapper.
- Build completo: `./mvnw clean verify` (compila, Checkstyle, testes, cobertura, regras de dependência).
- Um módulo: `./mvnw -pl leaktrace-<modulo> -am verify`.

## Fluxo

1. Pegue uma tarefa em `docs/BACKLOG.md`; identifique o módulo e o dono (`CLAUDE.md`, `.github/CODEOWNERS`).
2. Crie um branch a partir de `main`: `<tipo>/<modulo>-<resumo>`, ex.: `feat/rules-cpf-detector`.
3. Um PR por tarefa. Commits convencionais: `feat:`, `fix:`, `chore:`, `docs:`, `ci:`, `test:`, `refactor:`; escopo opcional com o módulo (`feat(rules): ...`).
4. Abra o PR com o template preenchido. Merge exige 1 aprovação, revisão do dono e CI verde (ver `docs/BRANCH_PROTECTION.md`).
5. Revisão cruzada: A revisa B, B revisa C, C revisa A.

## Regras que o build verifica

| Regra | Onde |
|-------|------|
| Módulo de biblioteca só depende de `leaktrace-core` e `leaktrace-annotations` | maven-enforcer |
| ByteBuddy só no `leaktrace-agent` | maven-enforcer |
| Só `leaktrace-reporter` escreve no terminal (`System.out/err`, `printStackTrace`) | Checkstyle |
| Cobertura mínima por módulo (80% linhas, 70% branches) | JaCoCo |
| Estilo: 4 espaços, 120 colunas, sem `import *` | Checkstyle, `.editorconfig` |

Baixar a meta de cobertura de um módulo exige justificativa no próprio `pom.xml` e aprovação do dono.

## Regras que o build não verifica

- **Dados sensíveis**: nunca use valores reais em código, testes, fixtures, logs, issues ou PRs. Use valores sintéticos (`DemoData`, `TestData`) e sempre exponha só a versão mascarada.
- **Contratos do core** (`TaintEvent`, `Sink`, `Rule`, `Reporter`): mudança exige aprovação dos três devs e, se relevante, um ADR novo em `docs/adr/`.
- **Agente**: nunca derrubar a aplicação. Toda lógica no caminho instrumentado captura `Throwable` e segue.
- **Testes**: use os fakes de `leaktrace-core` (classifier `test-fixtures`) em vez de depender de outros módulos.

## Decisões

Decisões de arquitetura vão em `docs/adr/` (ver `docs/adr/README.md`). Não reescreva um ADR aceito; crie outro que o substitua.
