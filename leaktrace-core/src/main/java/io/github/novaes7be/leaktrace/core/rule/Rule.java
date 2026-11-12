package io.github.novaes7be.leaktrace.core.rule;

import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import io.github.novaes7be.leaktrace.core.event.Severity;
import java.util.List;

/**
 * Detector de um tipo de dado sensível em texto (ex.: CPF com dígito verificador).
 *
 * <p>Responsabilidade: encontrar ocorrências e produzir a versão mascarada. Usada para marcar
 * fontes sem anotação e para varrer payloads em sinks.
 *
 * <p>Não deve:
 * <ul>
 *   <li>guardar, logar ou devolver o valor encontrado: {@link #find} retorna só posições;</li>
 *   <li>consultar configuração ou allowlist (o chamador filtra);</li>
 *   <li>ter estado mutável: é chamada concorrentemente;</li>
 *   <li>usar regex com backtracking catastrófico: roda em todo payload de sink.</li>
 * </ul>
 */
public interface Rule {

    /** Identificador estável, ex.: {@code br.cpf}. Usado na configuração e em relatórios. */
    String id();

    SensitiveType type();

    /** Severidade padrão; a configuração pode sobrescrever. */
    Severity defaultSeverity();

    /** Ocorrências em {@code text}, em ordem e sem sobreposição. Vazio se nada encontrado. */
    List<Match> find(CharSequence text);

    /**
     * Versão mascarada de um valor reconhecido por esta regra. Deve ser irreversível e não
     * revelar mais que o necessário para identificar o caso (ex.: dois últimos dígitos).
     */
    String mask(CharSequence value);
}
