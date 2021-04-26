package uk.co.agilesoftware.upstream;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.vavr.collection.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;
import uk.co.agilesoftware.upstream.model.LexiconMovies;
import uk.co.agilesoftware.upstream.model.Movie;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test"})
public class LexiconMovieConnectorTest {

    private static WireMockServer wireMockServer = new WireMockServer(9090);

    @Autowired
    private LexiconMovieConnector cineworldConnector;

    @BeforeAll
    public static void startWiremock() {
        WireMock.configureFor(9090);
        wireMockServer.start();
    }

    @BeforeEach
    public void resetWiremock() {
        wireMockServer.start();
    }

    @AfterAll
    public static void stopWiremock() {
        wireMockServer.stop();
    }

    @Test
    public void shouldLoadMoviesFromLexicon() {
        stubFor(get(urlPathEqualTo("/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(200).withBodyFile("lexiconResponses/movies.json").withHeader("Content-Type", "application/json")));

        LexiconMovies expectedMovies = new LexiconMovies("Film World", List.of(
                new Movie("fw2488496", "Star Wars: Episode VII - The Force Awakens", "Harrison Ford, Mark Hamill, Carrie Fisher, Adam Driver", 25.0),
                new Movie("fw2527336", "Star Wars: Episode VIII - The Last Jedi", "Mark Hamill, Carrie Fisher, Adam Driver, Daisy Ridley", 24.5)
        ));

        StepVerifier.create(cineworldConnector.getMovies())
                .expectNextMatches(actualMovies -> actualMovies.equals(expectedMovies))
                .verifyComplete();
    }

    @Test
    public void shouldFailIfUpstreamReturn4xx() {
        stubFor(get(urlPathEqualTo("/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(403).withBody("{\"message\": \"Forbidden\"}")));

        StepVerifier.create(cineworldConnector.getMovies())
                .expectErrorMatches(ex -> ex instanceof WebClientResponseException && ((WebClientResponseException) ex).getRawStatusCode() == 403)
                .verify();
    }

    @Test
    public void shouldFailIfUpstreamReturn5xx() {
        stubFor(get(urlPathEqualTo("/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(502)));

        StepVerifier.create(cineworldConnector.getMovies())
                .expectErrorMatches(ex -> ex instanceof WebClientResponseException && ((WebClientResponseException) ex).getRawStatusCode() == 502)
                .verify();
    }

}