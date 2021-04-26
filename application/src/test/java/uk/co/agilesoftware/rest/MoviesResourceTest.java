package uk.co.agilesoftware.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoviesResourceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void foo() {
        webTestClient.get().uri("/movies").exchange().expectStatus().isOk();
    }
}