package uk.co.agilesoftware.upstream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.collection.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LexiconMovies {

    private final String provider;
    private final List<Movie> movies;

    @JsonCreator
    public LexiconMovies(@JsonProperty("Provider") String provider,
                         @JsonProperty("Movies") List<Movie> movies) {
        this.provider = provider;
        this.movies = movies;
    }
}
