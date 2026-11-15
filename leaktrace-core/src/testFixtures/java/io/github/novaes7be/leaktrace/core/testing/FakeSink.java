package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.core.event.CodeLocation;
import io.github.novaes7be.leaktrace.core.sink.InterceptionPoint;
import io.github.novaes7be.leaktrace.core.sink.Sink;
import io.github.novaes7be.leaktrace.core.sink.SinkInvocation;
import io.github.novaes7be.leaktrace.core.sink.SinkOutcome;
import io.github.novaes7be.leaktrace.core.sink.SinkType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * {@link Sink} programável. Registra apenas os call sites inspecionados (nunca os argumentos,
 * seguindo o contrato) e devolve o desfecho calculado pela função dada.
 */
public final class FakeSink implements Sink {

    public static final String DEFAULT_ID = "fake.log";

    private final String id;
    private final SinkType type;
    private final Function<SinkInvocation, SinkOutcome> behavior;
    private final List<CodeLocation> inspectedCallSites = new CopyOnWriteArrayList<>();

    public FakeSink(String id, SinkType type, Function<SinkInvocation, SinkOutcome> behavior) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.behavior = Objects.requireNonNull(behavior);
    }

    /** Sink de log que sempre devolve {@code outcome}. */
    public static FakeSink returning(SinkOutcome outcome) {
        return new FakeSink(DEFAULT_ID, SinkType.LOG, invocation -> outcome);
    }

    /** Sink de log que nunca encontra nada. */
    public static FakeSink clean() {
        return returning(SinkOutcome.clean());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public SinkType type() {
        return type;
    }

    @Override
    public List<InterceptionPoint> interceptionPoints() {
        return List.of(new InterceptionPoint("demo.FakeLogger", "info", Optional.empty(), true));
    }

    @Override
    public SinkOutcome inspect(SinkInvocation invocation) {
        inspectedCallSites.add(invocation.callSite());
        return behavior.apply(invocation);
    }

    public List<CodeLocation> inspectedCallSites() {
        return List.copyOf(inspectedCallSites);
    }
}
