package io.github.novaes7be.leaktrace.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um valor como fonte de taint: tudo que for lido daqui passa a ser rastreado.
 *
 * <p>Exemplo:
 * <pre>{@code
 * record Customer(@Sensitive(type = SensitiveType.CPF) String cpf) {}
 * }</pre>
 *
 * <p>Retida em runtime porque o agente a lê por reflexão; o annotation processor também a lê em
 * tempo de build para validar o uso.
 *
 * <p>Não faz: não detecta padrões em texto livre (isso é papel das regras) e não altera o valor
 * por si só; a ação só é aplicada quando o valor chega a um sink.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.RECORD_COMPONENT})
public @interface Sensitive {

    /** Categoria do dado. */
    SensitiveType type();

    /** Ação aplicada quando o valor chega a um sink. */
    Action action() default Action.MASK;
}
