package io.github.novaes7be.leaktrace.core.event;

import java.util.Objects;

/**
 * Uma transformação pela qual o taint passou entre origem e sink.
 *
 * @param operation operação que propagou, ex.: {@code String.concat}, {@code StringBuilder.append}
 * @param location  onde a operação foi chamada
 */
public record PropagationStep(String operation, CodeLocation location) {

    public PropagationStep {
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(location, "location");
    }
}
