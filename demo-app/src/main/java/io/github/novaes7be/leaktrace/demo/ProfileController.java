package io.github.novaes7be.leaktrace.demo;

import io.github.novaes7be.leaktrace.annotations.Sensitive;
import io.github.novaes7be.leaktrace.annotations.SensitiveType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProfileController {

    record Profile(String username, String internalToken) {
    }

    @Sensitive(type = SensitiveType.TOKEN)
    private final String internalToken = DemoData.INTERNAL_TOKEN;

    @GetMapping("/api/me")
    Profile me() {
        // VAZAMENTO PLANTADO #2 (HTTP_RESPONSE): token interno de serviço devolvido ao cliente.
        return new Profile(DemoData.USERNAME, internalToken);
    }
}
