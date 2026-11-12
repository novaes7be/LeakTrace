package io.github.novaes7be.leaktrace.core.event;

import java.util.Objects;

/**
 * Ponto no código da aplicação. Equivalente serializável e estável de {@link StackTraceElement}.
 *
 * @param className  nome binário da classe
 * @param methodName nome do método
 * @param fileName   arquivo-fonte, ou {@code null} se desconhecido
 * @param lineNumber linha, ou {@code -1} se desconhecida
 */
public record CodeLocation(String className, String methodName, String fileName, int lineNumber) {

    /** Local desconhecido. */
    public static final CodeLocation UNKNOWN = new CodeLocation("?", "?", null, -1);

    public CodeLocation {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(methodName, "methodName");
        if (lineNumber < -1) {
            throw new IllegalArgumentException("lineNumber < -1");
        }
    }

    public static CodeLocation of(StackTraceElement frame) {
        return new CodeLocation(frame.getClassName(), frame.getMethodName(), frame.getFileName(),
            Math.max(frame.getLineNumber(), -1));
    }
}
