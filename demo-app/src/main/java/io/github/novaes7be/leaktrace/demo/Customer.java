package io.github.novaes7be.leaktrace.demo;

import io.github.novaes7be.leaktrace.annotations.Sensitive;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;

public record Customer(String name, @Sensitive(type = SensitiveType.CPF) String cpf) {
}
