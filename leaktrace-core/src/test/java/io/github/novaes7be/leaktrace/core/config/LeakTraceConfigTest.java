package io.github.novaes7be.leaktrace.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.novaes7be.leaktrace.core.event.Severity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LeakTraceConfigTest {

    @Test
    void rejectsNullLists() {
        assertThrows(NullPointerException.class,
            () -> new LeakTraceConfig(null, List.of(), false, Severity.LOW));
        assertThrows(NullPointerException.class,
            () -> new LeakTraceConfig(List.of(), null, false, Severity.LOW));
    }

    @Test
    void nullSeverityDefaultsToLow() {
        assertEquals(Severity.LOW, new LeakTraceConfig(List.of(), List.of(), false, null).minimumSeverity());
    }

    @Test
    void rejectsDuplicateRuleIds() {
        List<RuleConfig> rules = List.of(RuleConfig.enabled("br.cpf"), RuleConfig.enabled("br.cpf"));
        assertThrows(IllegalArgumentException.class,
            () -> new LeakTraceConfig(rules, List.of(), false, Severity.LOW));
    }

    @Test
    void copiesListsDefensively() {
        List<AllowlistEntry> allowlist = new ArrayList<>();
        allowlist.add(new AllowlistEntry(Optional.empty(), "com.acme.audit.*", "já mascara"));
        LeakTraceConfig config = new LeakTraceConfig(List.of(), allowlist, false, Severity.LOW);
        allowlist.clear();
        assertEquals(1, config.allowlist().size());
    }

    @Test
    void defaultsAreValid() {
        LeakTraceConfig config = LeakTraceConfig.defaults();
        assertEquals(Severity.LOW, config.minimumSeverity());
        assertEquals(List.of(), config.rules());
    }
}
