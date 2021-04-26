package uk.co.agilesoftware.rest;

import io.vavr.collection.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.co.agilesoftware.domain.Movie;

@RestController
@RequestMapping("/movies")
public class MoviesResource {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Movie>> getMovies() {
        return Mono.just(List.empty());
    }
}
