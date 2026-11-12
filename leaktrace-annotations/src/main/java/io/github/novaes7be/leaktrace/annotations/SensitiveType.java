package io.github.novaes7be.leaktrace.annotations;

/**
 * Categoria de dado sensível rastreado pelo LeakTrace.
 *
 * <p>Usado em {@link Sensitive}, nas regras de detecção e nos eventos de vazamento. Tipos não
 * cobertos pelas constantes específicas usam {@link #OTHER}; o identificador da regra no evento
 * diferencia casos dentro dessa categoria.
 */
public enum SensitiveType {
    /** CPF (pessoa física, Brasil). */
    CPF,
    /** CNPJ (pessoa jurídica, Brasil). */
    CNPJ,
    /** Número de cartão de pagamento (PAN). */
    CREDIT_CARD,
    /** Endereço de e-mail. */
    EMAIL,
    /** Número de telefone. */
    PHONE,
    /** Senha ou segredo digitado por usuário. */
    PASSWORD,
    /** Token de sessão, JWT, bearer ou refresh token. */
    TOKEN,
    /** Chave de API ou credencial de serviço. */
    API_KEY,
    /** Qualquer outro dado marcado como sensível. */
    OTHER
}
