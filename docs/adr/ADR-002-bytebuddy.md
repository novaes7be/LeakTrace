# ADR-002: ByteBuddy para instrumentação

- Status: Aceito
- Data: 2026-11-12
- Dono: A (agent)

## Contexto

O agente precisa instrumentar, em runtime, pontos de propagação no JDK (`String`, `StringBuilder`, `String.format`) e sinks em bibliotecas (SLF4J, Logback, Servlet/Spring, `Throwable`). Requisitos: suportar Java 17 a 25, retransformar classes já carregadas, overhead baixo e nunca derrubar a aplicação.

## Decisão

Usar **ByteBuddy** (`AgentBuilder` + `Advice`), exclusivamente no módulo `leaktrace-agent`.

- `Advice` inline (não delegation) para classes do JDK: retransformação não pode adicionar campos ou métodos, e `Advice` só insere bytecode no corpo existente.
- ByteBuddy é **relocado** pelo maven-shade-plugin (`io.github.novaes7be.leaktrace.shaded.bytebuddy`) para não colidir com a versão da aplicação (Mockito, Hibernate e Spring usam ByteBuddy).
- Classes auxiliares chamadas a partir do código instrumentado no JDK são injetadas no bootstrap (`Instrumentation#appendToBootstrapClassLoaderSearch`).
- Todo `Advice` envolve sua lógica em `try/catch(Throwable)` e engole o erro (com contador interno), para nunca propagar exceção para a aplicação.
- Os outros módulos descrevem o que interceptar com `InterceptionPoint` (core); só o agente traduz isso em matchers ByteBuddy. A regra é verificada no build (maven-enforcer).

## Consequências

- Positivas: API de alto nível e testável; suporte rápido a novas versões do JDK; `Advice` gera bytecode próximo de código escrito à mão.
- Negativas: jar do agente maior (~4 MB com shade); dependência de um projeto mantido por poucas pessoas; precisa acompanhar releases para cada nova versão do JDK (`net.bytebuddy.experimental` como saída temporária).

## Alternativas consideradas

- ASM direto: controle máximo e jar menor, mas muito mais código e difícil de revisar por três pessoas.
- Javassist: compila código-fonte em string, pior para depuração e mais lento em retransformação.
- AspectJ (load-time weaving): não instrumenta classes do JDK de forma prática e exige configuração por aplicação.
- `java.lang.classfile` (JEP 484): só a partir do Java 24; o alvo mínimo é 17.
