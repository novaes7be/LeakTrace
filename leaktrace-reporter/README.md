# leaktrace-reporter

- Responsabilidade: saída dos eventos: terminal em tempo real, resumo no shutdown, JSON e SARIF (ADR-003). Único módulo que escreve no terminal.
- Dono: C
- API pública: implementações de `Reporter`.
- Depende de: leaktrace-core, leaktrace-annotations.
