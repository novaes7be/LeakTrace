# Plano do LeakTrace 0.1.0

## Objetivo

Publicar no Maven Central (`io.github.novaes7be`) um Java agent que, anexado a uma aplicação com `-javaagent`, detecta em runtime dados sensíveis (CPF, cartão, token, senha) chegando a logs, respostas HTTP e mensagens de exceção, aplica a ação configurada (MASK, BLOCK, ALERT) e reporta no terminal, em JSON e em SARIF.

## Critério de sucesso da 0.1.0

1. Os três vazamentos plantados no `demo-app` são detectados com o agente: CPF no log, token na resposta e senha na exceção.
2. A ação padrão (MASK) impede que o valor real chegue ao sink; o modo só-alertar deixa passar e reporta.
3. O terminal mostra cada achado em tempo real e um resumo no shutdown; o SARIF é aceito pelo GitHub Code Scanning.
4. Nenhum valor real aparece em relatório, log do agente ou evento.
5. A aplicação nunca cai por causa do agente; overhead medido e publicado (meta inicial: p99 de requisição do demo-app até +15%).
6. Java 17, 21 e 25; Spring Boot 4 via starter.

## Equipe

| Dev | Módulos |
|-----|---------|
| A | `leaktrace-bom`, `leaktrace-annotations`, `leaktrace-core`, `leaktrace-taint-engine`, `leaktrace-agent` |
| B | `leaktrace-rules`, `leaktrace-processor`, `integration-tests` |
| C | `leaktrace-sinks`, `leaktrace-reporter`, `leaktrace-spring-boot-starter`, `leaktrace-action`, `demo-app`, `docs` |

Revisão cruzada: A revisa B, B revisa C, C revisa A. Contratos do core exigem os três.

## Cronograma

| Semana | Tema | Marco ao fim da semana |
|--------|------|------------------------|
| 1 | Fundação | Build multi-módulo, contratos, fakes, CI, demo-app com vazamentos (concluída) |
| 2 | Peças isoladas | Motor de taint, regras e reporter de terminal funcionando em testes unitários com fakes |
| 3 | Agente e primeiros sinks | Agente carrega, marca fontes `@Sensitive`, propaga em `String`/`StringBuilder`; sinks LOG e EXCEPTION implementados |
| 4 | Ponta a ponta | Agente aplica `SinkOutcome`; vazamentos #1 e #3 detectados e mascarados no demo-app; sink HTTP; reporter JSON |
| 5 | Integração e saída para CI | Vazamento #2 detectado; SARIF; starter Spring Boot; processor; benchmark de overhead |
| 6 | Release | GitHub Action, docs de uso, publicação 0.1.0 no Maven Central |

Detalhe por dev e critérios de aceite em [BACKLOG.md](BACKLOG.md). Arquitetura em [ARQUITETURA.md](ARQUITETURA.md); onde fica cada coisa em [MAPA.md](MAPA.md).

## Definição de pronto (toda tarefa)

- PR único, commits convencionais, CI verde (Java 17 e 21), 1 aprovação do revisor cruzado e do dono.
- Teste automatizado cobrindo o critério de aceite; cobertura do módulo dentro da meta (80% linhas, 70% branches).
- Regras de dependência respeitadas (verificadas no build).
- Nenhum valor real de dado sensível no código, em testes ou na saída.
- README do módulo atualizado se a API pública mudou.

## Riscos

| Risco | Impacto | Mitigação | Dono |
|-------|---------|-----------|------|
| Instrumentar `String`/`StringBuilder` no bootstrap gera overhead alto ou instabilidade | Alto | Advice mínimo, benchmark JMH desde a semana 3, opção de desligar propagação por operação | A |
| Perda de taint em caminhos não instrumentados (`char[]`, serializadores) | Médio | Regras por padrão varrem payloads nos sinks como segunda linha | B, C |
| Falsos positivos das regras (CPF/cartão por regex) | Médio | Dígito verificador/Luhn obrigatório, allowlist por localização, `@Ignore` | B |
| Conflito de versão com ByteBuddy/Jackson da aplicação | Alto | Shade com relocação no agente (ADR-002) | A |
| Mudanças de API entre versões do Spring Boot | Médio | Starter testado contra Boot 4.x; sinks HTTP no nível Servlet além do Spring | C |
| Contratos do core precisarem mudar após a semana 2 | Médio | Mudança só com os três devs e ADR; fakes atualizados no mesmo PR | A |
| Publicação no Central (assinatura, namespace) atrasar o release | Médio | Validar namespace e chave GPG na semana 5, dry-run de deploy | A |
