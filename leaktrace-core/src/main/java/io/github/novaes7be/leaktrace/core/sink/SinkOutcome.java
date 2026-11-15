package io.github.novaes7be.leaktrace.core.sink;

import io.github.novaes7be.leaktrace.core.event.TaintEvent;
import java.util.List;
import java.util.Map;

/**
 * Resultado de {@link Sink#inspect}: o que reportar e o que fazer com a chamada original.
 */
public sealed interface SinkOutcome {

    /** Eventos a reportar; vazio se nada vazou. */
    List<TaintEvent> events();

    /** Nada encontrado; segue a chamada original. */
    static SinkOutcome clean() {
        return new Proceed(List.of());
    }

    /** Segue a chamada original sem alteração (ação ALERT ou nada encontrado). */
    record Proceed(List<TaintEvent> events) implements SinkOutcome {
        public Proceed {
            events = List.copyOf(events);
        }
    }

    /**
     * Segue a chamada com argumentos substituídos (ação MASK).
     *
     * @param replacements índice do argumento para o novo valor (não nulo)
     */
    record Replace(List<TaintEvent> events, Map<Integer, Object> replacements) implements SinkOutcome {
        public Replace {
            events = List.copyOf(events);
            replacements = Map.copyOf(replacements);
            if (replacements.isEmpty()) {
                throw new IllegalArgumentException("Replace sem substituições; use Proceed");
            }
        }
    }

    /** Não executa a chamada original (ação BLOCK). */
    record Block(List<TaintEvent> events) implements SinkOutcome {
        public Block {
            events = List.copyOf(events);
            if (events.isEmpty()) {
                throw new IllegalArgumentException("Block exige ao menos um evento");
            }
        }
    }
}
