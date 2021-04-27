package uk.co.agilesoftware.domain;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import uk.co.agilesoftware.upstream.model.LexiconMovies;

public class CombinedMovies {
    private Map<String, List<Movie>> movies;

    public CombinedMovies(LexiconMovies moviesOne, LexiconMovies moviesTwo) {
        movies = moviesOne.getMovies()
                .merge(moviesTwo.getMovies(), List::appendAll)
                .mapValues(List::sorted);
    }

    public List<CombinedMovie> get() {
        return movies.map(combined -> new CombinedMovie(combined._1, combined._2())).toList();
    }
}
