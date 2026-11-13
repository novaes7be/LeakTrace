package io.github.novaes7be.leaktrace.core.config;

import io.github.novaes7be.leaktrace.core.event.Severity;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Configuração imutável do LeakTrace, espelho do {@code leaktrace.yml}.
 *
 * <p>Este módulo só define o modelo; a leitura do YAML fica no agente/starter, para o core
 * continuar sem dependências.
 *
 * <pre>{@code
 * leaktrace:
 *   alert-only: false
 *   minimum-severity: MEDIUM
 *   rules:
 *     - id: br.cpf
 *       severity: CRITICAL
 *   allowlist:
 *     - location: com.acme.audit.*
 *       reason: logger de auditoria já mascara
 * }</pre>
 *
 * @param rules           ajustes por regra, ids únicos; regras não listadas usam o padrão.
 *                        Não nulo (o leitor de YAML converte ausência em lista vazia)
 * @param allowlist       exceções conhecidas. Não nulo
 * @param alertOnly       se {@code true}, toda ação vira ALERT (nada é mascarado ou bloqueado)
 * @param minimumSeverity eventos abaixo deste nível não são reportados; {@code null} vira LOW
 */
public record LeakTraceConfig(List<RuleConfig> rules, List<AllowlistEntry> allowlist,
                              boolean alertOnly, Severity minimumSeverity) {

    public LeakTraceConfig {
        rules = List.copyOf(Objects.requireNonNull(rules, "rules"));
        allowlist = List.copyOf(Objects.requireNonNull(allowlist, "allowlist"));
        if (minimumSeverity == null) {
            minimumSeverity = Severity.LOW;
        }
        Set<String> ids = new HashSet<>();
        for (RuleConfig rule : rules) {
            if (!ids.add(rule.id())) {
                throw new IllegalArgumentException("regra duplicada na configuração: " + rule.id());
            }
        }
    }

    /** Configuração usada quando não há {@code leaktrace.yml}. */
    public static LeakTraceConfig defaults() {
        return new LeakTraceConfig(List.of(), List.of(), false, Severity.LOW);
    }
}
