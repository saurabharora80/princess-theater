package uk.co.agilesoftware.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.agilesoftware.config.UpstreamProperties;
import uk.co.agilesoftware.service.MovieService;
import uk.co.agilesoftware.upstream.LexiconMovieConnector;

@Configuration
public class ApplicationBeans {

    @Bean
    public UpstreamProperties upstreamConfig() {
        return new UpstreamProperties();
    }

    @Bean
    public WebClientLoggingInterceptor webClientLoggingInterceptor() {
        return new WebClientLoggingInterceptor();
    }

    @Bean
    public WebClientCreator webClientCreator(final ObjectMapper objectMapper,
                                             final UpstreamProperties upstreamProperties,
                                             final WebClientLoggingInterceptor loggingInterceptor) {
        return new WebClientCreator(objectMapper, upstreamProperties, loggingInterceptor);
    }

    @Bean
    public LexiconMovieConnector cinemaworldConnector(WebClientCreator webClientCreator,
                                                      UpstreamProperties upstreamProperties) {
        return getLexiconMovieConnector(webClientCreator, upstreamProperties, "cinemaworld");
    }

    @Bean
    public LexiconMovieConnector filmworldConnector(WebClientCreator webClientCreator,
                                                    UpstreamProperties upstreamProperties) {
        return getLexiconMovieConnector(webClientCreator, upstreamProperties, "filmworld");
    }

    private LexiconMovieConnector getLexiconMovieConnector(WebClientCreator webClientCreator, UpstreamProperties upstreamProperties, String provider) {
        return new LexiconMovieConnector(provider,
                webClientCreator.createClient("lexicon", provider),
                upstreamProperties.getClient("lexicon").getApiKey()
        );
    }

    @Bean
    public MovieService movieService(LexiconMovieConnector cinemaworldConnector, LexiconMovieConnector filmworldConnector) {
        return new MovieService(cinemaworldConnector, filmworldConnector);
    }
}
