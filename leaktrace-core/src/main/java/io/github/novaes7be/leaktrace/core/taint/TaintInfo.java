package io.github.novaes7be.leaktrace.core.taint;

import io.github.novaes7be.leaktrace.annotations.Action;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.event.Origin;
import io.github.novaes7be.leaktrace.core.event.PropagationStep;
import java.util.List;
import java.util.Objects;

/**
 * O que o motor sabe sobre um valor rastreado.
 *
 * @param type            categoria do dado
 * @param action          ação declarada na origem
 * @param origin          origem do taint
 * @param propagationPath caminho até aqui
 */
public record TaintInfo(SensitiveType type, Action action, Origin origin,
                        List<PropagationStep> propagationPath) {

    public TaintInfo {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(origin, "origin");
        propagationPath = List.copyOf(propagationPath);
    }
}
