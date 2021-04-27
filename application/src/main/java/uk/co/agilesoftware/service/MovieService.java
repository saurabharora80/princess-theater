package uk.co.agilesoftware.service;

import reactor.core.publisher.Mono;
import uk.co.agilesoftware.domain.CombinedMovies;
import uk.co.agilesoftware.upstream.LexiconMovieConnector;

public class MovieService {

    private final LexiconMovieConnector cineworldConnector;

    private final LexiconMovieConnector filmworldConnector;

    public MovieService(LexiconMovieConnector cineworldConnector, LexiconMovieConnector filmworldConnector) {
        this.cineworldConnector = cineworldConnector;
        this.filmworldConnector = filmworldConnector;
    }

    public Mono<CombinedMovies> getCombinedMovies() {
        return Mono.zip(cineworldConnector.getMovies(), filmworldConnector.getMovies())
                .map(allMovies -> new CombinedMovies(allMovies.getT1(), allMovies.getT2()));
    }

}
