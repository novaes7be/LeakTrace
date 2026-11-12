package io.github.novaes7be.leaktrace.core.event;

import java.util.Objects;

/**
 * De onde veio o taint.
 *
 * @param kind       como o valor foi marcado
 * @param identifier campo/parâmetro anotado ({@code com.acme.Customer#cpf}) ou id da regra
 * @param location   onde a marcação ocorreu, ou {@link CodeLocation#UNKNOWN}
 */
public record Origin(Kind kind, String identifier, CodeLocation location) {

    public enum Kind {
        /** Valor lido de elemento com {@code @Sensitive}. */
        ANNOTATION,
        /** Valor reconhecido por uma {@code Rule}. */
        RULE
    }

    public Origin {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(location, "location");
    }
}
