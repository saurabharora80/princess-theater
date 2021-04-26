package uk.co.agilesoftware.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.agilesoftware.config.UpstreamProperties;
import uk.co.agilesoftware.service.MovieService;
import uk.co.agilesoftware.upstream.LexiconMovieConnector;

@Configuration
public class ServiceBeans {

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
    public LexiconMovieConnector cineworldConnector(WebClientCreator webClientCreator,
                                                    UpstreamProperties upstreamProperties) {
        return new LexiconMovieConnector(
                webClientCreator.createClient("lexicon", "cineworld"),
                upstreamProperties.getClient("lexicon").getApiKey()
        );
    }

    @Bean
    public LexiconMovieConnector filmworldConnector(WebClientCreator webClientCreator,
                                                    UpstreamProperties upstreamProperties) {
        return new LexiconMovieConnector(
                webClientCreator.createClient("lexicon", "filmworld"),
                upstreamProperties.getClient("lexicon").getApiKey()
        );
    }

    @Bean
    public MovieService movieService(LexiconMovieConnector cineworldConnector, LexiconMovieConnector filmworldConnector) {
        return new MovieService(cineworldConnector, filmworldConnector);
    }
}
