package io.github.novaes7be.leaktrace.core.sink;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SinkOutcomeTest {

    @Test
    void cleanHasNoEvents() {
        assertTrue(SinkOutcome.clean().events().isEmpty());
    }

    @Test
    void replaceRequiresReplacements() {
        assertThrows(IllegalArgumentException.class, () -> new SinkOutcome.Replace(List.of(), Map.of()));
    }

    @Test
    void blockRequiresEvent() {
        assertThrows(IllegalArgumentException.class, () -> new SinkOutcome.Block(List.of()));
    }
}
