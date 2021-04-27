package uk.co.agilesoftware.service;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.agilesoftware.domain.CombinedMovie;
import uk.co.agilesoftware.domain.CombinedMovies;
import uk.co.agilesoftware.upstream.LexiconMovieConnector;
import uk.co.agilesoftware.upstream.model.LexiconMovie;
import uk.co.agilesoftware.upstream.model.LexiconMovies;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class LexiconMovieServiceTest {

    @Mock
    private LexiconMovieConnector filmworldConnector;
    @Mock
    private LexiconMovieConnector cineworldConnector;

    @Test
    public void combinedTheMoviesFetchedFromUpstream() {
        LexiconMovies filmworldMovies = new LexiconMovies("Film World", List.of(
                new LexiconMovie("Star Wars: Episode VII - The Force Awakens", "Harrison Ford, Mark Hamill, Carrie Fisher, Adam Driver", 25.0),
                new LexiconMovie("Star Wars: Episode VIII - The Last Jedi", "Mark Hamill, Carrie Fisher, Adam Driver, Daisy Ridley", 24.5)
        ));

        LexiconMovies cineworldMovies = new LexiconMovies("Cine World", List.of(
                new LexiconMovie("Star Wars: Episode VII - The Force Awakens", "Harrison Ford, Mark Hamill, Carrie Fisher, Adam Driver", 26.0),
                new LexiconMovie("Star Wars: Episode VIII - The Last Jedi", "Mark Hamill, Carrie Fisher, Adam Driver, Daisy Ridley", 24.0)
        ));

        given(cineworldConnector.getMovies()).willReturn(Mono.just(cineworldMovies));
        given(filmworldConnector.getMovies()).willReturn(Mono.just(filmworldMovies));

        MovieService service = new MovieService(cineworldConnector, filmworldConnector);

        Mono<List<CombinedMovie>> combinedMovies = service.getCombinedMovies().map(CombinedMovies::get);

        Mono<CombinedMovie> starWarsEpisode7 = combinedMovies.map(movies -> movies.find(m -> m.getTitle().equalsIgnoreCase("Star Wars: Episode VII - The Force Awakens")).get());

        Mono<CombinedMovie> starWarsEpisode8 = combinedMovies.map(movies -> movies.find(m -> m.getTitle().equalsIgnoreCase("Star Wars: Episode VIII - The Last Jedi")).get());

        StepVerifier.create(starWarsEpisode7)
            .expectNextMatches(m -> m.getMovies().size() == 2)
            .verifyComplete();

        StepVerifier.create(starWarsEpisode8)
                .expectNextMatches(m -> m.getMovies().size() == 2)
                .verifyComplete();

    }

    @Test
    public void returnsEmptyWhenUpstreamReturnsEmpty() {
        given(cineworldConnector.getMovies()).willReturn(Mono.empty());
        given(filmworldConnector.getMovies()).willReturn(Mono.empty());

        MovieService service = new MovieService(cineworldConnector, filmworldConnector);

        StepVerifier.create(service.getCombinedMovies()).verifyComplete();
    }


}