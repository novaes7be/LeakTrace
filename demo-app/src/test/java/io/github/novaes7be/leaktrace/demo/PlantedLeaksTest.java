package io.github.novaes7be.leaktrace.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Linha de base sem agente: os três vazamentos acontecem. Quando o agente existir, os testes
 * em integration-tests devem provar o oposto (valor mascarado, bloqueado ou reportado).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class PlantedLeaksTest {

    @LocalServerPort
    int port;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void leak1CpfReachesLog(CapturedOutput output) throws Exception {
        HttpResponse<String> response = post("/api/customers",
            "{\"name\":\"Fulano\",\"cpf\":\"" + DemoData.CPF + "\"}");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(output.getOut()).contains(DemoData.CPF);
    }

    @Test
    void leak2TokenReachesHttpResponse() throws Exception {
        HttpResponse<String> response = http.send(
            HttpRequest.newBuilder(uri("/api/me")).GET().build(), HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains(DemoData.INTERNAL_TOKEN);
    }

    @Test
    void leak3PasswordReachesExceptionMessage() throws Exception {
        String wrongPassword = "senha-errada-0000";
        HttpResponse<String> response = post("/api/login",
            "{\"username\":\"demo\",\"password\":\"" + wrongPassword + "\"}");

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.body()).contains(wrongPassword);
    }

    @Test
    void correctPasswordIsAccepted() throws Exception {
        HttpResponse<String> response = post("/api/login",
            "{\"username\":\"demo\",\"password\":\"" + DemoData.PASSWORD + "\"}");

        assertThat(response.statusCode()).isEqualTo(200);
    }

    private HttpResponse<String> post(String path, String json) throws Exception {
        return http.send(HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(),
            HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
