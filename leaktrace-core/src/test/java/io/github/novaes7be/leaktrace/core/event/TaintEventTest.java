package io.github.novaes7be.leaktrace.core.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.sink.SinkType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaintEventTest {

    private static final Origin ORIGIN =
        new Origin(Origin.Kind.ANNOTATION, "demo.Customer#cpf", CodeLocation.UNKNOWN);
    private static final SinkRef SINK = new SinkRef("log.slf4j", SinkType.LOG, CodeLocation.UNKNOWN);

    @Test
    void copiesPropagationPathDefensively() {
        List<PropagationStep> path = new ArrayList<>();
        path.add(new PropagationStep("String.concat", CodeLocation.UNKNOWN));

        TaintEvent event = new TaintEvent(SensitiveType.CPF, "***.***.***-00", ORIGIN, path, SINK,
            Severity.HIGH, Instant.EPOCH);
        path.clear();

        assertEquals(1, event.propagationPath().size());
        assertThrows(UnsupportedOperationException.class, () -> event.propagationPath().clear());
    }

    @Test
    void rejectsMissingFields() {
        assertThrows(NullPointerException.class, () -> new TaintEvent(SensitiveType.CPF, null, ORIGIN,
            List.of(), SINK, Severity.HIGH, Instant.EPOCH));
    }

    @Test
    void severityOrdering() {
        assertTrue(Severity.CRITICAL.isAtLeast(Severity.HIGH));
        assertTrue(Severity.MEDIUM.isAtLeast(Severity.MEDIUM));
        assertTrue(!Severity.LOW.isAtLeast(Severity.MEDIUM));
    }
}
