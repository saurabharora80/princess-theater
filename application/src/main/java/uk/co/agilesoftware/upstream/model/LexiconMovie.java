package uk.co.agilesoftware.upstream.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class LexiconMovie {

    @Getter
    private final String title;

    private final String actors;

    @Getter
    private final Double price;

    @JsonCreator
    public LexiconMovie(@JsonProperty("Title") String title,
                        @JsonProperty("Actors") String actors,
                        @JsonProperty("Price") Double price) {
        this.title = title;
        this.actors = actors;
        this.price = price;
    }

}
