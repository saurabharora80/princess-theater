package uk.co.agilesoftware.upstream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.agilesoftware.domain.Movie;

@EqualsAndHashCode
@ToString
public class LexiconMovies {

    private final String provider;

    private final List<LexiconMovie> movies;

    @JsonCreator
    public LexiconMovies(@JsonProperty("Provider") String provider,
                         @JsonProperty("Movies") List<LexiconMovie> movies) {
        this.provider = provider;
        this.movies = movies;
    }

    public Map<String, List<Movie>> getMovies() {
        return movies.map(m -> new Movie(provider, m)).groupBy(Movie::getTitle);
    }

}
