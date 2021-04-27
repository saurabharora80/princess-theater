package uk.co.agilesoftware.upstream;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import uk.co.agilesoftware.WiremockTest;
import uk.co.agilesoftware.upstream.model.LexiconMovie;
import uk.co.agilesoftware.upstream.model.LexiconMovies;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test"})
public class LexiconLexiconMovieConnectorTest extends WiremockTest {

    @Autowired
    private LexiconMovieConnector filmworldConnector;

    @Test
    public void shouldLoadMoviesFromLexicon() {
        stubFor(get(urlPathEqualTo("/filmworld/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(200).withBodyFile("lexiconResponses/filmworldmovies.json").withHeader("Content-Type", "application/json")));

        LexiconMovies expectedMovies = new LexiconMovies("Film World", List.of(
                new LexiconMovie("Star Wars: Episode VII - The Force Awakens", "Harrison Ford, Mark Hamill, Carrie Fisher, Adam Driver", 25.0),
                new LexiconMovie("Star Wars: Episode VIII - The Last Jedi", "Mark Hamill, Carrie Fisher, Adam Driver, Daisy Ridley", 24.5)
        ));

        StepVerifier.create(filmworldConnector.getMovies())
                .expectNextMatches(actualMovies -> actualMovies.equals(expectedMovies))
                .verifyComplete();
    }

    @Test
    public void shouldFailIfUpstreamReturn4xx() {
        stubFor(get(urlPathEqualTo("/filmworld/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(403).withBody("{\"message\": \"Forbidden\"}")));

        StepVerifier.create(filmworldConnector.getMovies())
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldFailIfUpstreamReturn5xx() {
        stubFor(get(urlPathEqualTo("/filmworld/movies")).withHeader("x-api-key", equalTo("api-key-value"))
                .willReturn(aResponse().withStatus(502)));

        StepVerifier.create(filmworldConnector.getMovies())
                .expectComplete()
                .verify();
    }

}