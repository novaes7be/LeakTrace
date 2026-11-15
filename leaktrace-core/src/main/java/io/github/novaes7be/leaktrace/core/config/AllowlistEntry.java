package io.github.novaes7be.leaktrace.core.config;

import java.util.Objects;
import java.util.Optional;

/**
 * Exceção conhecida ({@code allowlist:} no YAML): achados que casam são descartados antes do
 * reporter. Não contém valores de dados, só localização, para a configuração nunca guardar
 * dado sensível.
 *
 * @param ruleId          restringe a uma regra; vazio vale para todas
 * @param locationPattern glob sobre {@code classe#metodo} do call site, ex.: {@code com.acme.audit.*}
 * @param reason          justificativa obrigatória
 */
public record AllowlistEntry(Optional<String> ruleId, String locationPattern, String reason) {

    public AllowlistEntry {
        Objects.requireNonNull(ruleId, "ruleId");
        Objects.requireNonNull(locationPattern, "locationPattern");
        Objects.requireNonNull(reason, "reason");
        if (locationPattern.isBlank() || reason.isBlank()) {
            throw new IllegalArgumentException("locationPattern e reason são obrigatórios");
        }
    }
}
