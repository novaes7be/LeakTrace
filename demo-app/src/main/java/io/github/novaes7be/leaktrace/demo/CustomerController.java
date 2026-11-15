package io.github.novaes7be.leaktrace.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void register(@RequestBody Customer customer) {
        // VAZAMENTO PLANTADO #1 (LOG): CPF completo no log.
        log.info("Cliente cadastrado: nome={} cpf={}", customer.name(), customer.cpf());
    }
}
