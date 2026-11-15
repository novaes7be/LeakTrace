package io.github.novaes7be.leaktrace.core.taint;

import java.util.Optional;

/**
 * Visão somente-leitura do motor de taint, entregue aos sinks.
 *
 * <p>Responsabilidade: responder se um objeto específico (por identidade, ver ADR-001) está
 * marcado. Implementado pelo {@code leaktrace-taint-engine}.
 *
 * <p>Não deve: marcar ou desmarcar valores, comparar por {@code equals} (dois objetos com o
 * mesmo conteúdo não compartilham taint) nem lançar exceção; em erro, retorna vazio.
 */
@FunctionalInterface
public interface TaintLookup {

    /** Informação de taint do objeto, ou vazio se não rastreado. Aceita {@code null}. */
    Optional<TaintInfo> lookup(Object value);
}
