package io.github.novaes7be.leaktrace.core.event;

import io.github.novaes7be.leaktrace.core.sink.SinkType;
import java.util.Objects;

/**
 * Identifica o sink atingido em um evento.
 *
 * @param sinkId   {@code Sink#id()} do sink que detectou
 * @param type     categoria do sink
 * @param callSite código da aplicação que fez a escrita
 */
public record SinkRef(String sinkId, SinkType type, CodeLocation callSite) {

    public SinkRef {
        Objects.requireNonNull(sinkId, "sinkId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(callSite, "callSite");
    }
}
