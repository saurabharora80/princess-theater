package uk.co.agilesoftware.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test"})
class MoviesResourceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getMovies() {
        webTestClient.get().uri("/movies").exchange().expectStatus().isOk();
    }
}