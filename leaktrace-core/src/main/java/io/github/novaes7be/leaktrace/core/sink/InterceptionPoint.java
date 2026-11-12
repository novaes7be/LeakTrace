package io.github.novaes7be.leaktrace.core.sink;

import java.util.Objects;
import java.util.Optional;

/**
 * Método que o agente deve instrumentar para alimentar um sink. Declarativo: o sink diz
 * <em>o quê</em>, o agente decide <em>como</em> (ByteBuddy fica só no agente).
 *
 * @param className     nome binário da classe ou interface
 * @param methodName    nome do método
 * @param descriptor    descritor JVM para um overload específico; vazio = todos os overloads
 * @param matchSubtypes também instrumentar implementações/subclasses
 */
public record InterceptionPoint(String className, String methodName, Optional<String> descriptor,
                                boolean matchSubtypes) {

    public InterceptionPoint {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(methodName, "methodName");
        Objects.requireNonNull(descriptor, "descriptor");
    }
}
