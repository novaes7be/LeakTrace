package io.github.novaes7be.leaktrace.core.report;

import io.github.novaes7be.leaktrace.core.event.TaintEvent;

/**
 * Destino dos eventos: terminal, JSON, SARIF.
 *
 * <p>Responsabilidade: receber eventos em tempo real e, em {@link #close()}, emitir o resumo
 * ou gravar o arquivo final.
 *
 * <p>Não deve:
 * <ul>
 *   <li>bloquear em {@link #report}: é chamado na thread da aplicação; enfileire se for lento;</li>
 *   <li>lançar exceção para o chamador;</li>
 *   <li>desfazer o mascaramento ou enriquecer o evento com dados da aplicação;</li>
 *   <li>filtrar por severidade ou allowlist (feito antes de chegar aqui).</li>
 * </ul>
 *
 * <p>Implementações devem ser thread-safe.
 */
public interface Reporter extends AutoCloseable {

    /** Identificador estável, ex.: {@code terminal}, {@code sarif}. */
    String id();

    void report(TaintEvent event);

    /** Fim da execução (shutdown da JVM). Idempotente; não lança exceção. */
    @Override
    void close();
}
