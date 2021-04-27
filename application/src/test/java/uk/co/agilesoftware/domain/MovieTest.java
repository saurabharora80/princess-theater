package uk.co.agilesoftware.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MovieTest {

    @ParameterizedTest
    @CsvSource({
            "Cinema World,CinemaWorld",
            "Cinema World ,CinemaWorld",
            " Cinema World ,CinemaWorld"
    })
    public void dropAnyEmptySpaceInProviderName(String originalProviderName, String formattedProviderName) {
        assertThat(new Movie(originalProviderName, "", 25.0).getProvider()).isEqualTo(formattedProviderName);
    }
}