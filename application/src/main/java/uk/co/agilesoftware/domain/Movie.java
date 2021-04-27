package uk.co.agilesoftware.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import uk.co.agilesoftware.upstream.model.LexiconMovie;

@EqualsAndHashCode
@ToString
public class Movie implements Comparable<Movie> {

    private final String provider;

    @Getter
    private final String title;

    @Getter
    private final Double price;

    public Movie(String provider, LexiconMovie movie) {
        this(provider, movie.getTitle(), movie.getPrice());
    }

    protected Movie(String provider, String title, Double price) {
        this.provider = provider;
        this.title = title;
        this.price = price;
    }

    public String getProvider() {
        return provider.replaceAll("\\s+", "");
    }

    @Override
    public int compareTo(Movie that) {
        return this.price.compareTo(that.price);
    }
}
