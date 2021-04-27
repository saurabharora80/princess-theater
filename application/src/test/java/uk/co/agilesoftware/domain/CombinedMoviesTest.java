package uk.co.agilesoftware.domain;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import uk.co.agilesoftware.upstream.model.LexiconMovies;
import uk.co.agilesoftware.upstream.model.LexiconMovie;

import static org.assertj.core.api.Assertions.assertThat;

class CombinedMoviesTest {

    @Test
    public void combinedMoviesByTitleAndOrderByPrice() {
        String starWarsEpisode7 = "Star Wars: Episode VII";

        String starWarsEpisode8 = "Star Wars: Episode VIII";

        LexiconMovies cineworldMovies = new LexiconMovies("Cinema World", List.of(
                new LexiconMovie(starWarsEpisode7, "Harrison Ford, Mark Hamill", 25.0),
                new LexiconMovie(starWarsEpisode8, "Mark Hamill, Carrie Fisher", 26.0)
        ));

        LexiconMovies filmworldMovies = new LexiconMovies("Film World", List.of(
                new LexiconMovie(starWarsEpisode7, "Harrison Ford, Mark Hamill", 24.0),
                new LexiconMovie(starWarsEpisode8, "Mark Hamill, Carrie Fisher", 27.0)
        ));

        CombinedMovies actualCombinedMovies = new CombinedMovies(cineworldMovies, filmworldMovies);

        assertThat(getCombinedMovies(starWarsEpisode7, actualCombinedMovies))
                .containsExactly(
                        new Movie("Film World", starWarsEpisode7, 24.0), new Movie("Cinema World", starWarsEpisode7, 25.0)
                );

        assertThat(getCombinedMovies(starWarsEpisode8, actualCombinedMovies))
                .containsExactly(
                        new Movie("Cinema World", starWarsEpisode8, 26.0), new Movie("Film World", starWarsEpisode8, 27.0)
                );
    }

    @Test
    public void combinedMoviesByTitleAndOrderByPrice_WhenMovieOnlyExistInOneList() {
        String starWarsEpisode7 = "Star Wars: Episode VII";

        String starWarsEpisode8 = "Star Wars: Episode VIII";

        String starWarsEpisode9 = "Star Wars: Episode IX";

        LexiconMovies cineworldMovies = new LexiconMovies("Cinema World", List.of(
                new LexiconMovie(starWarsEpisode7, "Harrison Ford, Mark Hamill", 25.0),
                new LexiconMovie(starWarsEpisode8, "Mark Hamill, Carrie Fisher", 26.0)
        ));

        LexiconMovies filmworldMovies = new LexiconMovies("Film World", List.of(
                new LexiconMovie(starWarsEpisode7, "Harrison Ford, Mark Hamill", 24.0),
                new LexiconMovie(starWarsEpisode8, "Mark Hamill, Carrie Fisher", 27.0),
                new LexiconMovie(starWarsEpisode9, "Adam Driver, Daisy Ridley", 28.0)
        ));

        CombinedMovies actualCombinedMovies = new CombinedMovies(cineworldMovies, filmworldMovies);

        assertThat(getCombinedMovies(starWarsEpisode7, actualCombinedMovies))
                .containsExactly(
                        new Movie("Film World", starWarsEpisode7, 24.0), new Movie("Cinema World", starWarsEpisode7, 25.0)
                );

        assertThat(getCombinedMovies(starWarsEpisode8, actualCombinedMovies))
                .containsExactly(
                        new Movie("Cinema World", starWarsEpisode8, 26.0), new Movie("Film World", starWarsEpisode8, 27.0)
                );

        assertThat(getCombinedMovies(starWarsEpisode9, actualCombinedMovies))
                .containsExactly(
                        new Movie("Film World", starWarsEpisode9, 28.0)
                );
    }

    @Test
    public void combinedMoviesByTitleAndOrderByPrice_WhenOneMoviesListIsEmpty() {
        String starWarsEpisode7 = "Star Wars: Episode VII";

        String starWarsEpisode8 = "Star Wars: Episode VIII";

        String starWarsEpisode9 = "Star Wars: Episode IX";

        LexiconMovies cineworldMovies = new LexiconMovies("Cineworld", List.empty());

        LexiconMovies filmworldMovies = new LexiconMovies("Filmworld", List.of(
                new LexiconMovie(starWarsEpisode7, "Harrison Ford, Mark Hamill", 24.0),
                new LexiconMovie(starWarsEpisode8, "Mark Hamill, Carrie Fisher", 27.0),
                new LexiconMovie(starWarsEpisode9, "Adam Driver, Daisy Ridley", 28.0)
        ));

        CombinedMovies actualCombinedMovies = new CombinedMovies(cineworldMovies, filmworldMovies);

        assertThat(getCombinedMovies(starWarsEpisode7, actualCombinedMovies))
                .containsExactly(
                        new Movie("Filmworld", starWarsEpisode7, 24.0)
                );

        assertThat(getCombinedMovies(starWarsEpisode8, actualCombinedMovies))
                .containsExactly(
                        new Movie("Filmworld", starWarsEpisode8, 27.0)
                );

        assertThat(getCombinedMovies(starWarsEpisode9, actualCombinedMovies))
                .containsExactly(
                        new Movie("Filmworld", starWarsEpisode9, 28.0)
                );
    }

    @Test
    public void combinedMoviesByTitleAndOrderByPrice_WhenBothOneMoviesListIsEmpty() {
        LexiconMovies cineworldMovies = new LexiconMovies("Cineworld", List.empty());

        LexiconMovies filmworldMovies = new LexiconMovies("Filmworld", List.empty());

        CombinedMovies actualCombinedMovies = new CombinedMovies(cineworldMovies, filmworldMovies);

        assertThat(actualCombinedMovies.get()).isEmpty();
    }

    private List<Movie> getCombinedMovies(String starWarsEpisode7, CombinedMovies actualCombinedMovies) {
        return actualCombinedMovies.get().find(m -> m.getTitle().equalsIgnoreCase(starWarsEpisode7)).get().getMovies();
    }

}