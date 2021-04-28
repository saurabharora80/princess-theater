package uk.co.agilesoftware.upstream;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import uk.co.agilesoftware.upstream.model.LexiconMovies;

import java.time.Duration;

@RequiredArgsConstructor
public class LexiconMovieConnector {

    private final String provider;

    private final WebClient webClient;

    private final String apiKey;

    private final Integer retryAttempts;

    private final Duration retryDuration;

    public Mono<LexiconMovies> getMovies() {
        return webClient.get().uri("/{provider}/movies", provider).header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(LexiconMovies.class)
                .retryWhen(Retry.fixedDelay(retryAttempts, retryDuration))
                .onErrorResume(ex -> Mono.empty());
    }
}
