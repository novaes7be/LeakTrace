package io.github.novaes7be.leaktrace.annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SensitiveTest {

    record Sample(@Sensitive(type = SensitiveType.TOKEN) String token) {}

    @Test
    void isVisibleAtRuntimeWithMaskAsDefault() throws Exception {
        Sensitive sensitive = Sample.class.getDeclaredField("token").getAnnotation(Sensitive.class);
        assertEquals(SensitiveType.TOKEN, sensitive.type());
        assertEquals(Action.MASK, sensitive.action());
    }
}
