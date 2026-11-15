package io.github.novaes7be.leaktrace.core.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.novaes7be.leaktrace.annotations.Action;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.rule.Match;
import io.github.novaes7be.leaktrace.core.sink.SinkOutcome;
import java.util.List;
import org.junit.jupiter.api.Test;

class FakesTest {

    @Test
    void fakeRuleFindsEveryMarkerAndNeverEchoesIt() {
        FakeRule rule = new FakeRule();
        String text = "a " + FakeRule.DEFAULT_MARKER + " b " + FakeRule.DEFAULT_MARKER;

        assertEquals(List.of(new Match(2, 18), new Match(21, 37)), rule.find(text));
        assertFalse(rule.mask(FakeRule.DEFAULT_MARKER).contains(FakeRule.DEFAULT_MARKER));
    }

    @Test
    void fakeTaintLookupUsesIdentity() {
        FakeTaintLookup lookup = new FakeTaintLookup();
        String tainted = lookup.taint(new String("valor"), TestData.taintInfo(SensitiveType.TOKEN, Action.MASK));

        assertTrue(lookup.lookup(tainted).isPresent());
        assertFalse(lookup.lookup(new String("valor")).isPresent());
        assertFalse(lookup.lookup(null).isPresent());
    }

    @Test
    void fakeSinkRecordsCallSiteAndReturnsOutcome() {
        SinkOutcome block = new SinkOutcome.Block(List.of(TestData.event()));
        FakeSink sink = FakeSink.returning(block);

        assertEquals(block, sink.inspect(FakeSinkInvocation.of(new FakeTaintLookup(), "msg")));
        assertEquals(List.of(TestData.CALL_SITE), sink.inspectedCallSites());
    }

    @Test
    void fakeReporterCollectsEvents() {
        FakeReporter reporter = new FakeReporter();
        reporter.report(TestData.event());
        reporter.close();

        assertEquals(1, reporter.events().size());
        assertEquals(1, reporter.closeCalls());
    }
}
