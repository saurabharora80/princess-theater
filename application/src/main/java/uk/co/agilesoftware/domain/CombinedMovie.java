package uk.co.agilesoftware.domain;

import io.vavr.collection.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CombinedMovie {
    @Getter
    private final String title;
    @Getter
    private final List<Movie> movies;
}
