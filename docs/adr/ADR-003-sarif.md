# ADR-003: SARIF 2.1.0 como formato de relatório para CI

- Status: Aceito
- Data: 2026-11-12
- Dono: C (reporter, action)

## Contexto

Além do terminal, os achados precisam ser consumidos em CI: anotação no PR, painel de segurança e comparação entre execuções. Precisamos de um formato padrão que ferramentas existentes entendam.

## Decisão

Gerar **SARIF 2.1.0** (OASIS) no `leaktrace-reporter`, além de um JSON próprio mais simples para scripts.

Mapeamento:

| LeakTrace | SARIF |
|-----------|-------|
| `Rule#id` / tipo da anotação | `reportingDescriptor.id` em `tool.driver.rules` |
| `Severity` LOW / MEDIUM | `level: note` / `warning` |
| `Severity` HIGH / CRITICAL | `level: error` |
| `SinkRef.callSite` | `locations[0].physicalLocation` |
| `Origin` + `propagationPath` | `codeFlows[0].threadFlows[0].locations` |
| hash de (tipo, origem, sink, callSite) | `partialFingerprints.leaktrace/v1` |

Regras:

- A mensagem usa somente `maskedValue`. Nenhum valor real em `message`, `properties` ou `snippet`; não gerar `region.snippet`.
- `partialFingerprints` não inclui o valor (nem mascarado), para que o mesmo vazamento com dados diferentes deduplique.
- A `leaktrace-action` publica o SARIF via `github/codeql-action/upload-sarif` e comenta o resumo no PR.

## Consequências

- Positivas: integração direta com GitHub Code Scanning, VS Code SARIF Viewer e outras ferramentas; `codeFlows` representa naturalmente o caminho de propagação.
- Negativas: SARIF é verboso; caminhos de classe precisam ser mapeados para arquivo-fonte (`CodeLocation.fileName` + pacote), o que pode falhar para classes geradas.

## Alternativas consideradas

- Só JSON próprio: simples, mas sem integração com ferramentas.
- JUnit XML: aceito por todo CI, porém não representa localização nem fluxo.
- CodeClimate/GitLab Code Quality: útil para GitLab; pode ser adicionado depois como outro `Reporter`.
