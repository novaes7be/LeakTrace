package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.annotations.Action;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.event.CodeLocation;
import io.github.novaes7be.leaktrace.core.event.Origin;
import io.github.novaes7be.leaktrace.core.event.PropagationStep;
import io.github.novaes7be.leaktrace.core.event.Severity;
import io.github.novaes7be.leaktrace.core.event.SinkRef;
import io.github.novaes7be.leaktrace.core.event.TaintEvent;
import io.github.novaes7be.leaktrace.core.sink.SinkType;
import io.github.novaes7be.leaktrace.core.taint.TaintInfo;
import java.time.Instant;
import java.util.List;

/** Valores prontos e sintéticos para testes. */
public final class TestData {

    /** Instante fixo para testes determinísticos. */
    public static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    public static final CodeLocation CALL_SITE =
        new CodeLocation("demo.CustomerService", "register", "CustomerService.java", 42);

    public static final Origin ORIGIN =
        new Origin(Origin.Kind.ANNOTATION, "demo.Customer#cpf", CodeLocation.UNKNOWN);

    private TestData() {
    }

    public static TaintInfo taintInfo(SensitiveType type, Action action) {
        return new TaintInfo(type, action, ORIGIN, List.of());
    }

    public static TaintEvent event(SensitiveType type, Severity severity) {
        return new TaintEvent(type, "[MASKED]", ORIGIN,
            List.of(new PropagationStep("String.concat", CALL_SITE)),
            new SinkRef(FakeSink.DEFAULT_ID, SinkType.LOG, CALL_SITE), severity, NOW);
    }

    public static TaintEvent event() {
        return event(SensitiveType.CPF, Severity.HIGH);
    }
}
