package io.github.novaes7be.leaktrace.core.event;

/** Gravidade de um vazamento, em ordem crescente. Mapeia para níveis SARIF (ver ADR-003). */
public enum Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    /** {@code true} se esta severidade é igual ou maior que {@code threshold}. */
    public boolean isAtLeast(Severity threshold) {
        return compareTo(threshold) >= 0;
    }
}
