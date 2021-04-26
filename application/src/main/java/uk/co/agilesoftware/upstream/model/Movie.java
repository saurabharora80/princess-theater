package uk.co.agilesoftware.upstream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Movie {

    private final String id;
    private final String title;
    private final String actors;
    private final Double price;

    @JsonCreator
    public Movie(@JsonProperty("ID") String id,
                 @JsonProperty("Title") String title,
                 @JsonProperty("Actors") String actors,
                 @JsonProperty("Price") Double price) {
        this.id = id;
        this.title = title;
        this.actors = actors;
        this.price = price;
    }
}
