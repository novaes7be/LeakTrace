package io.github.novaes7be.leaktrace.core.event;

import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Um vazamento detectado: dado sensível chegou a um sink. Valor imutável, seguro para
 * compartilhar entre threads e serializar.
 *
 * <p>Responsabilidade: carregar tudo que um {@code Reporter} precisa para explicar o achado
 * sem consultar outro componente.
 *
 * <p>Não deve: conter o valor real do dado. {@code maskedValue} é sempre a saída de
 * {@code Rule#mask}; quem constrói o evento é responsável por isso. Também não carrega
 * referências para objetos da aplicação, para não segurá-los na memória.
 *
 * @param dataType        categoria do dado
 * @param maskedValue     valor já mascarado, ex.: {@code ***.***.***-09}
 * @param origin          onde o taint começou
 * @param propagationPath transformações da origem até o sink, em ordem; pode ser vazio
 * @param sink            sink atingido
 * @param severity        gravidade efetiva (após overrides de configuração)
 * @param timestamp       momento da detecção
 */
public record TaintEvent(
    SensitiveType dataType,
    String maskedValue,
    Origin origin,
    List<PropagationStep> propagationPath,
    SinkRef sink,
    Severity severity,
    Instant timestamp) {

    public TaintEvent {
        Objects.requireNonNull(dataType, "dataType");
        Objects.requireNonNull(maskedValue, "maskedValue");
        Objects.requireNonNull(origin, "origin");
        propagationPath = List.copyOf(propagationPath);
        Objects.requireNonNull(sink, "sink");
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(timestamp, "timestamp");
    }
}
