# Política de segurança

## Versões suportadas

| Versão | Suporte |
|--------|---------|
| 0.x (SNAPSHOT) | sem garantias; correções vão para `main` |

## Como reportar

Use o reporte privado do GitHub: **Security > Report a vulnerability** em https://github.com/novaes7be/LeakTrace/security/advisories/new.

Não abra issue pública para:

- o LeakTrace vazando ou persistindo valor real de dado sensível (relatório, log, SARIF, exceção);
- o agente derrubando, travando ou alterando o comportamento da aplicação instrumentada;
- forma de contornar o mascaramento ou o bloqueio.

**Não inclua dados reais** no reporte. Reproduza com valores sintéticos.

## O que esperar

- Confirmação de recebimento em até 5 dias úteis.
- Avaliação e plano de correção em até 15 dias úteis.
- Crédito no advisory, se desejar.

## Escopo

O LeakTrace é uma ferramenta de detecção e não substitui revisão de código, DLP ou controles de acesso. Falso negativo (vazamento não detectado) é bug comum, não vulnerabilidade, salvo se causado por contorno deliberado do mecanismo.
