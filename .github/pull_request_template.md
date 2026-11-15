## O que muda

<!-- Uma frase. Link para a tarefa do docs/BACKLOG.md. -->

## Módulo e dono

- Módulo:
- Dono:
- Revisor (revisão cruzada A→B, B→C, C→A):

## Checklist

- [ ] Um PR por tarefa, commits convencionais (`feat:`, `fix:`, `chore:`, `docs:`, `ci:`)
- [ ] `./mvnw clean verify` verde localmente
- [ ] Código novo com teste; cobertura do módulo dentro da meta
- [ ] Só depende de `leaktrace-core` / `leaktrace-annotations` (ou é módulo de montagem)
- [ ] Não altera contrato do core (`TaintEvent`, `Sink`, `Rule`, `Reporter`) — ou tem aprovação dos 3 devs
- [ ] Nenhum valor real de dado sensível em código, teste, log, fixture ou descrição deste PR
- [ ] Docs atualizados se mudou comportamento (README do módulo, ARQUITETURA, ADR)
