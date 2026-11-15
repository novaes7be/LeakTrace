package io.github.novaes7be.leaktrace.sinks;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.novaes7be.leaktrace.core.testing.FakeSink;
import io.github.novaes7be.leaktrace.core.testing.FakeSinkInvocation;
import io.github.novaes7be.leaktrace.core.testing.FakeTaintLookup;
import org.junit.jupiter.api.Test;

/** Prova que os fakes do core estão disponíveis. Remover quando houver a primeira suíte real. */
class SinksPlaceholderTest {

    @Test
    void coreTestFixturesAreOnTheClasspath() {
        FakeSink sink = FakeSink.clean();
        assertTrue(sink.inspect(FakeSinkInvocation.of(new FakeTaintLookup(), "msg")).events().isEmpty());
    }
}
