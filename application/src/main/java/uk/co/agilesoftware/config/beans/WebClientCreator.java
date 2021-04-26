package uk.co.agilesoftware.config.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.co.agilesoftware.config.Endpoint;
import uk.co.agilesoftware.config.UpstreamProperties;
import uk.co.agilesoftware.config.UpstreamProperties.HttpClientConfig;
import uk.co.agilesoftware.config.UpstreamProperties.HttpClientConfigs;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import static org.springframework.http.HttpHeaders.HOST;
import static org.springframework.http.HttpHeaders.USER_AGENT;

@Slf4j
public class WebClientCreator {

    private final ObjectMapper mapper;

    private final UpstreamProperties upstreamProperties;

    private final WebClientLoggingInterceptor loggingInterceptor;

    public WebClientCreator(ObjectMapper mapper,
                            UpstreamProperties upstreamProperties,
                            WebClientLoggingInterceptor loggingInterceptor) {
        this.mapper = mapper;
        this.upstreamProperties = upstreamProperties;
        this.loggingInterceptor = loggingInterceptor;
    }

    public WebClient createClient(String clientName) {
        return createClient(upstreamProperties.getClient(clientName));
    }

    public WebClient createClient(String clientName, String resourceName) {
        HttpClientConfigs clientConf = upstreamProperties.getClient(clientName);
        HttpClientConfig resourceConf = clientConf.getResource(resourceName);
        return createClient(resourceConf);
    }

    private WebClient createClient(HttpClientConfig resourceConf) {

        Endpoint endpoint = resourceConf.getEndpoint().orElseThrow(() -> new RuntimeException("Endpoint not defined"));

        String endpointUrl = endpoint.getUrlString();

        String endpointHost = endpoint.getHost();
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies(resourceConf, mapper))
                .baseUrl(endpointUrl)
                .defaultHeaders(headersConfigurer(upstreamProperties.getUserAgentHeader(), endpointHost))
                .clientConnector(create(endpointHost, resourceConf))
                .filter(loggingInterceptor)
                .build();
    }

    private static final MimeType JSON_MIME_TYPE = MimeTypeUtils.parseMimeType(MediaType.APPLICATION_JSON_VALUE);

    private static Consumer<HttpHeaders> headersConfigurer(String userAgent, String host) {
        return headers -> {
            headers.add(USER_AGENT, userAgent);
            headers.add(HOST, host);
        };
    }

    private static <T extends HttpClientConfig> ExchangeStrategies exchangeStrategies(T conf, ObjectMapper mapper) {
        return ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, JSON_MIME_TYPE)))
                .codecs(codecs -> codecs.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, JSON_MIME_TYPE)))
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(conf.getMaxInMemoryBufferMb() * 1024 * 1024))
                .build();
    }

    private static ReactorClientHttpConnector create(String name, UpstreamProperties.HttpClientConfig conf) {

        ConnectionProvider connectionProvider = ConnectionProvider.builder("webclient-conn-" + name)
                .maxConnections(conf.getMaxConnections())
                .maxIdleTime(Duration.of(conf.getMaxConnectionIdleSeconds(), ChronoUnit.SECONDS))
                .metrics(conf.getEnableMicrometerMetric())
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .keepAlive(true)
                .tcpConfiguration(client ->
                        client.metrics(conf.getEnableMicrometerMetric())
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, conf.getConnectTimeoutSeconds() * 1000)
                                .doOnConnected(conn -> conn
                                        .addHandlerLast(new ReadTimeoutHandler(conf.getReadWriteTimeoutSeconds()))
                                        .addHandlerLast(new WriteTimeoutHandler(conf.getReadWriteTimeoutSeconds()))
                                )
                );
        return new ReactorClientHttpConnector(httpClient);
    }

}
