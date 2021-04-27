package uk.co.agilesoftware.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.co.agilesoftware.WiremockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test"})
class MoviesResourceTest extends WiremockTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getMovies() {

        stubFor(get(urlPathEqualTo("/filmworld/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(200).withBodyFile("lexiconResponses/filmworldmovies.json").withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathEqualTo("/cinemaworld/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(200).withBodyFile("lexiconResponses/cinemaworldmovies.json").withHeader("Content-Type", "application/json")));

        webTestClient.get().uri("/movies")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[*].title").value(Matchers.contains("Star Wars: Episode VII - The Force Awakens", "Star Wars: Episode VIII - The Last Jedi"))
                .jsonPath("$.[0].movies[*].provider").value(Matchers.contains("CinemaWorld", "FilmWorld"))
                .jsonPath("$.[0].movies[*].price").value(Matchers.contains(24.7, 25.0))
                .jsonPath("$.[1].movies[*].provider").value(Matchers.contains("CinemaWorld", "FilmWorld"))
                .jsonPath("$.[1].movies[*].price").value(Matchers.contains(24.0, 24.5));

    }
}

