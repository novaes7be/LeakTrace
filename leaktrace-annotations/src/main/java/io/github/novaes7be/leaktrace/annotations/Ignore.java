package io.github.novaes7be.leaktrace.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Suprime achados do LeakTrace no elemento anotado.
 *
 * <p>Em um campo ou parâmetro: o valor não vira fonte de taint mesmo que uma regra o detecte
 * (falso positivo conhecido). Em um método ou tipo: escritas em sinks feitas a partir dali não
 * geram evento (por exemplo, um logger de auditoria que já mascara).
 *
 * <p>Não faz: não remove taint de valores que já estavam marcados ao entrar no escopo, e não
 * desliga o agente; o rastreio continua fora do elemento anotado.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER,
    ElementType.RECORD_COMPONENT})
public @interface Ignore {

    /** Tipos suprimidos. Vazio significa todos. */
    SensitiveType[] types() default {};

    /** Justificativa obrigatória, exibida em relatórios para auditoria. */
    String reason();
}
