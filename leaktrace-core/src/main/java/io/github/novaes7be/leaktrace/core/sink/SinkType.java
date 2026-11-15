package io.github.novaes7be.leaktrace.core.sink;

/** Categoria de saída monitorada. */
public enum SinkType {
    /** Frameworks de log (SLF4J, Logback, Log4j2, JUL, System.out/err). */
    LOG,
    /** Corpo, headers ou status de resposta HTTP. */
    HTTP_RESPONSE,
    /** Mensagem de exceção que pode chegar a log ou cliente. */
    EXCEPTION
}
