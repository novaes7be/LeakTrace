package io.github.novaes7be.leaktrace.core.sink;

import io.github.novaes7be.leaktrace.core.event.CodeLocation;
import io.github.novaes7be.leaktrace.core.taint.TaintLookup;

/**
 * Uma chamada interceptada em um {@link InterceptionPoint}, entregue a {@link Sink#inspect}.
 *
 * <p>Interface (não record) para o agente expor os argumentos sem copiar o array a cada chamada.
 * Válida apenas durante {@code inspect}; o sink não deve guardar a referência.
 */
public interface SinkInvocation {

    InterceptionPoint point();

    int argumentCount();

    /** Argumento na posição {@code index}; pode ser {@code null}. */
    Object argument(int index);

    /** Código da aplicação que fez a chamada. */
    CodeLocation callSite();

    /** Consulta de taint para os argumentos. */
    TaintLookup taint();
}
