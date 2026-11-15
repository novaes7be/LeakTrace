package io.github.novaes7be.leaktrace.core.testing;

import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.event.Severity;
import io.github.novaes7be.leaktrace.core.rule.Match;
import io.github.novaes7be.leaktrace.core.rule.Rule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** {@link Rule} que reconhece um marcador literal. Sem regex, sem estado. */
public final class FakeRule implements Rule {

    public static final String DEFAULT_MARKER = "FAKE-SECRET-0001";

    private final String id;
    private final SensitiveType type;
    private final Severity severity;
    private final String marker;

    public FakeRule(String id, SensitiveType type, Severity severity, String marker) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.severity = Objects.requireNonNull(severity);
        if (marker.isEmpty()) {
            throw new IllegalArgumentException("marker vazio");
        }
        this.marker = marker;
    }

    /** Regra {@code fake.token}, TOKEN, HIGH, marcador {@link #DEFAULT_MARKER}. */
    public FakeRule() {
        this("fake.token", SensitiveType.TOKEN, Severity.HIGH, DEFAULT_MARKER);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public SensitiveType type() {
        return type;
    }

    @Override
    public Severity defaultSeverity() {
        return severity;
    }

    @Override
    public List<Match> find(CharSequence text) {
        String s = text.toString();
        List<Match> matches = new ArrayList<>();
        int from = 0;
        int at;
        while ((at = s.indexOf(marker, from)) >= 0) {
            matches.add(new Match(at, at + marker.length()));
            from = at + marker.length();
        }
        return List.copyOf(matches);
    }

    @Override
    public String mask(CharSequence value) {
        return "[MASKED:" + type + "]";
    }
}
