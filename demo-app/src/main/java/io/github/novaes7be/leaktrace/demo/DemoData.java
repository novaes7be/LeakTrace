package io.github.novaes7be.leaktrace.demo;

/**
 * Valores sintéticos usados pelo demo. Nenhum corresponde a pessoa ou credencial real.
 * Os testes de integração procuram exatamente estes valores nas saídas.
 */
public final class DemoData {

    /** CPF de teste amplamente usado em documentação; dígitos verificadores válidos. */
    public static final String CPF = "111.444.777-35";

    /** Token interno fictício de serviço. */
    public static final String INTERNAL_TOKEN = "lt_demo_tok_0000000000000000";

    /** Usuário e senha fictícios. */
    public static final String USERNAME = "demo";
    public static final String PASSWORD = "senha-demo-0000";

    private DemoData() {
    }
}
