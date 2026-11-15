package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.core.event.CodeLocation;
import io.github.novaes7be.leaktrace.core.sink.InterceptionPoint;
import io.github.novaes7be.leaktrace.core.sink.SinkInvocation;
import io.github.novaes7be.leaktrace.core.taint.TaintLookup;
import java.util.Objects;
import java.util.Optional;

/** {@link SinkInvocation} montada à mão para testar sinks sem o agente. */
public final class FakeSinkInvocation implements SinkInvocation {

    private final InterceptionPoint point;
    private final CodeLocation callSite;
    private final TaintLookup taint;
    private final Object[] arguments;

    public FakeSinkInvocation(InterceptionPoint point, CodeLocation callSite, TaintLookup taint,
                              Object... arguments) {
        this.point = Objects.requireNonNull(point);
        this.callSite = Objects.requireNonNull(callSite);
        this.taint = Objects.requireNonNull(taint);
        this.arguments = arguments.clone();
    }

    /** Invocação em {@code demo.FakeLogger#info} no {@link TestData#CALL_SITE}. */
    public static FakeSinkInvocation of(TaintLookup taint, Object... arguments) {
        return new FakeSinkInvocation(
            new InterceptionPoint("demo.FakeLogger", "info", Optional.empty(), true),
            TestData.CALL_SITE, taint, arguments);
    }

    @Override
    public InterceptionPoint point() {
        return point;
    }

    @Override
    public int argumentCount() {
        return arguments.length;
    }

    @Override
    public Object argument(int index) {
        return arguments[index];
    }

    @Override
    public CodeLocation callSite() {
        return callSite;
    }

    @Override
    public TaintLookup taint() {
        return taint;
    }
}
