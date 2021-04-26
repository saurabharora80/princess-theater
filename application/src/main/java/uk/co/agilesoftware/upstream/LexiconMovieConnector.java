package uk.co.agilesoftware.upstream;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class LexiconMovieConnector {

    private final WebClient webClient;
}
