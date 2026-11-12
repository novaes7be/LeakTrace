package io.github.novaes7be.leaktrace.annotations;

/**
 * O que o LeakTrace faz quando um dado sensível chega a um sink.
 *
 * <p>Todas as ações geram um evento de vazamento. A configuração global pode forçar
 * {@link #ALERT} para tudo (modo só-alertar), o que sobrepõe a ação declarada.
 */
public enum Action {
    /** Substitui o valor pela versão mascarada antes de chegar ao sink e reporta. */
    MASK,
    /** Impede a escrita no sink e reporta. */
    BLOCK,
    /** Deixa a escrita seguir sem alteração e apenas reporta. */
    ALERT
}
