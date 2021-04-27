package uk.co.agilesoftware.rest;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.co.agilesoftware.domain.CombinedMovie;
import uk.co.agilesoftware.domain.CombinedMovies;
import uk.co.agilesoftware.service.MovieService;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MoviesResource {

    private final MovieService movieService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<CombinedMovie>> getMovies() {
        return movieService.getCombinedMovies().map(CombinedMovies::get);
    }
}
