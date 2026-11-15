package io.github.novaes7be.leaktrace.core.config;

import io.github.novaes7be.leaktrace.annotations.Action;
import io.github.novaes7be.leaktrace.core.event.Severity;
import java.util.Objects;
import java.util.Optional;

/**
 * Ajuste de uma regra na configuração ({@code rules:} no YAML).
 *
 * @param id       {@code Rule#id()} alvo
 * @param enabled  {@code false} desliga a regra
 * @param severity sobrescreve {@code Rule#defaultSeverity()}; vazio mantém o padrão
 * @param action   ação para achados desta regra; vazio usa a ação da anotação ou MASK
 */
public record RuleConfig(String id, boolean enabled, Optional<Severity> severity,
                         Optional<Action> action) {

    public RuleConfig {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id de regra vazio");
        }
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(action, "action");
    }

    /** Regra ligada, sem overrides. */
    public static RuleConfig enabled(String id) {
        return new RuleConfig(id, true, Optional.empty(), Optional.empty());
    }
}
