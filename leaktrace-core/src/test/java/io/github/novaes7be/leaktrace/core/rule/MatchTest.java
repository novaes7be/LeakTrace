package io.github.novaes7be.leaktrace.core.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MatchTest {

    @Test
    void lengthIsEndMinusStart() {
        assertEquals(11, new Match(3, 14).length());
    }

    @Test
    void rejectsEmptyOrNegativeRange() {
        assertThrows(IllegalArgumentException.class, () -> new Match(5, 5));
        assertThrows(IllegalArgumentException.class, () -> new Match(-1, 2));
    }
}
