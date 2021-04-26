package uk.co.agilesoftware.upstream;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.agilesoftware.upstream.model.LexiconMovies;

@RequiredArgsConstructor
public class LexiconMovieConnector {

    private final WebClient webClient;

    private final String apiKey;

    public Mono<LexiconMovies> getMovies() {
        return webClient.get().uri("/movies").header("x-api-key", apiKey).retrieve().bodyToMono(LexiconMovies.class);
    }
}
