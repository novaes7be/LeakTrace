package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.core.taint.TaintInfo;
import io.github.novaes7be.leaktrace.core.taint.TaintLookup;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link TaintLookup} por identidade, como o motor real (ADR-001), mas com referências fortes:
 * adequado só para testes curtos.
 */
public final class FakeTaintLookup implements TaintLookup {

    private final Map<Object, TaintInfo> tainted = new IdentityHashMap<>();

    /** Marca {@code value} e o devolve, para uso inline. */
    public synchronized <T> T taint(T value, TaintInfo info) {
        tainted.put(value, info);
        return value;
    }

    @Override
    public synchronized Optional<TaintInfo> lookup(Object value) {
        return value == null ? Optional.empty() : Optional.ofNullable(tainted.get(value));
    }
}
