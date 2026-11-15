package io.github.novaes7be.leaktrace.core.sink;

import java.util.List;

/**
 * Ponto de saída monitorado (log, HTTP, exception).
 *
 * <p>Responsabilidade: declarar quais métodos interceptar e, a cada chamada interceptada,
 * verificar se algum argumento está marcado e decidir o desfecho conforme a ação.
 *
 * <p>Não deve:
 * <ul>
 *   <li>usar ByteBuddy ou instrumentar classes (papel do agente);</li>
 *   <li>escrever em log, terminal ou rede (papel do reporter), inclusive para depuração;</li>
 *   <li>lançar exceção para a aplicação: em erro interno, retornar {@link SinkOutcome#clean()};</li>
 *   <li>colocar o valor real em {@code TaintEvent} ou guardar a {@link SinkInvocation};</li>
 *   <li>fazer trabalho caro no caminho comum (sem taint): é chamado em toda escrita.</li>
 * </ul>
 *
 * <p>Implementações devem ser thread-safe e sem estado mutável compartilhado.
 */
public interface Sink {

    /** Identificador estável, ex.: {@code log.slf4j}. Aparece em eventos e na configuração. */
    String id();

    SinkType type();

    /** Métodos a instrumentar. Consultado uma vez, na inicialização do agente. */
    List<InterceptionPoint> interceptionPoints();

    /** Inspeciona uma chamada interceptada. Nunca retorna {@code null}. */
    SinkOutcome inspect(SinkInvocation invocation);
}
