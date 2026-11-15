package io.github.novaes7be.leaktrace.demo;

import io.github.novaes7be.leaktrace.annotations.Sensitive;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {

    record LoginRequest(String username, @Sensitive(type = SensitiveType.PASSWORD) String password) {
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class InvalidCredentialsException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvalidCredentialsException(String message) {
            super(message);
        }
    }

    @PostMapping("/api/login")
    void login(@RequestBody LoginRequest request) {
        if (!DemoData.PASSWORD.equals(request.password())) {
            // VAZAMENTO PLANTADO #3 (EXCEPTION): senha digitada na mensagem da exception,
            // que chega ao corpo do erro (server.error.include-message=always).
            throw new InvalidCredentialsException(
                "Credenciais invalidas: usuario=" + request.username() + " senha=" + request.password());
        }
    }
}
