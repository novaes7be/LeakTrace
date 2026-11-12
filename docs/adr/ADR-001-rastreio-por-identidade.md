# ADR-001: Rastreio de taint por identidade com mapa de chaves fracas

- Status: Aceito
- Data: 2026-11-12
- Dono: A (taint-engine)

## Contexto

O motor precisa saber se um objeto que chega a um sink carrega dado sensível. Há duas famílias de abordagem:

1. Taint embutido no objeto (campo extra em `String`, `StringBuilder`, etc.). Exige alterar o layout de classes do JDK, o que a JVM não permite em retransformação, e quebra com `-Xshare`/CDS.
2. Taint externo: um mapa objeto -> `TaintInfo` mantido pelo motor.

Com o mapa externo, a chave precisa:

- comparar por **identidade** (`==`), não por `equals`: duas `String` com o mesmo conteúdo não podem compartilhar taint, senão um literal `"123.456.789-09"` em teste marcaria toda ocorrência igual;
- ser **fraca**: o mapa não pode impedir o GC de coletar objetos da aplicação.

## Decisão

Usar um mapa de **chaves fracas comparadas por identidade** (`WeakIdentityMap`, implementação própria no `leaktrace-taint-engine`), com `WeakReference` + `System.identityHashCode` + `ReferenceQueue` para limpar entradas coletadas.

Observação: `java.util.WeakHashMap`, citado no plano original, tem chaves fracas mas compara por `equals`/`hashCode`. Para `String` isso viola o requisito de identidade. Ele não é usado diretamente; o nome do ADR mantém a intenção (mapa fraco), não a classe.

Regras adicionais:

- Concorrência: o mapa é acessado de qualquer thread da aplicação. Começar com lock striping (N segmentos); medir antes de otimizar.
- Não marcar strings internadas nem constantes de compilação (são compartilhadas por toda a JVM).
- Propagação: operações instrumentadas (`concat`, `StringBuilder.append`, `String.format`, etc.) copiam o `TaintInfo` do operando para o resultado e acrescentam um `PropagationStep`.
- O valor real nunca é armazenado no mapa além da própria chave fraca.

## Consequências

- Positivas: sem alterar layout de classes do JDK; sem vazamento de memória causado pelo agente; semântica precisa (sem falsos positivos por conteúdo igual).
- Negativas: custo de lookup em todo sink e em cada operação de propagação; perda de taint quando a aplicação reconstrói o dado caractere a caractere (ex.: `char[]` manual) — mitigado pelas regras de detecção por padrão nos sinks.
- Dependências: o mapa vive no classloader de bootstrap para ser visível pelos pontos instrumentados no JDK (ver ADR-002).

## Alternativas consideradas

- `WeakHashMap`: descartado pela comparação por `equals` e por não ser thread-safe.
- `IdentityHashMap`: identidade correta, mas chaves fortes (vazamento de memória).
- Caffeine `weakKeys()`: compara por identidade e é concorrente, mas adiciona dependência ao agente e precisaria ser relocada e carregada no bootstrap. Reavaliar se a implementação própria não atingir a meta de overhead.
- Campo de taint em `String`: impossível sem modificar o JDK.
