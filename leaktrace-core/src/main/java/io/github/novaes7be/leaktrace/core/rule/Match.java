package io.github.novaes7be.leaktrace.core.rule;

/**
 * Trecho reconhecido por uma {@link Rule}. Guarda só posições, nunca o conteúdo.
 *
 * @param start índice inicial, inclusivo
 * @param end   índice final, exclusivo
 */
public record Match(int start, int end) {

    public Match {
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("intervalo inválido: [" + start + ", " + end + ")");
        }
    }

    public int length() {
        return end - start;
    }
}
