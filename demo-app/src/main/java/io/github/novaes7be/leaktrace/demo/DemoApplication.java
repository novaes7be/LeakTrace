package io.github.novaes7be.leaktrace.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação com três vazamentos plantados, alvo dos testes do LeakTrace.
 * Todos os dados são sintéticos (ver {@link DemoData}).
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
