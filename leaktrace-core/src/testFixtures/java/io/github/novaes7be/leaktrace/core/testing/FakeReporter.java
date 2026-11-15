package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.core.event.TaintEvent;
import io.github.novaes7be.leaktrace.core.report.Reporter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** {@link Reporter} que guarda os eventos em memória, thread-safe. */
public final class FakeReporter implements Reporter {

    private final List<TaintEvent> events = new CopyOnWriteArrayList<>();
    private final AtomicInteger closeCalls = new AtomicInteger();

    @Override
    public String id() {
        return "fake";
    }

    @Override
    public void report(TaintEvent event) {
        events.add(event);
    }

    @Override
    public void close() {
        closeCalls.incrementAndGet();
    }

    public List<TaintEvent> events() {
        return List.copyOf(events);
    }

    public int closeCalls() {
        return closeCalls.get();
    }
}
