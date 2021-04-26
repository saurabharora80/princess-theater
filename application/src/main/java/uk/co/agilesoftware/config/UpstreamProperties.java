package uk.co.agilesoftware.config;

import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@ToString
@ConfigurationProperties("upstream")
public class UpstreamProperties {

    private String userAgentHeader;

    private Map<String, HttpClientConfigs> client;

    public String getUserAgentHeader() {
        return userAgentHeader;
    }

    public void setUserAgentHeader(String userAgentHeader) {
        this.userAgentHeader = userAgentHeader;
    }

    public HttpClientConfigs getClient(String clientKey) {
        return requireNonNull(client.get(clientKey), "Could not find client: " + clientKey + " config");
    }

    public void setClient(Map<String, HttpClientConfigs> client) {
        this.client = client;
    }

    public interface BasicHttpClientConfig {
        Integer getMaxConnections();

        Integer getMaxConnectionIdleSeconds();

        Optional<Endpoint> getEndpoint();

        Integer getConnectTimeoutSeconds();

        Integer getReadWriteTimeoutSeconds();

        Integer getMaxInMemoryBufferMb();

        Boolean getEnableMicrometerMetric();
    }

    @ToString
    public static class HttpClientConfigs extends HttpClientConfig {
        private Map<String, HttpClientConfig> resources;

        public void setResources(Map<String, HttpClientConfig> resources) {
            resources.values().forEach(config -> config.setParent(this));
            this.resources = resources;
        }

        public HttpClientConfig getResource(String key) {
            return Optional.ofNullable(resources.get(key))
                    .orElseThrow(() -> new RuntimeException("Resource not defined for: " + key));
        }

    }

    @ToString
    public static class HttpClientConfig implements BasicHttpClientConfig {

        private static final int DEFAULT_CONNECT_TIMEOUT_SECS = 3;

        private static final int DEFAULT_READ_TIMEOUT_SECS = 6;

        private static final int DEFAULT_MAX_IN_MEMORY_BUFFER_MB = 10;

        public static final int DEFAULT_MAX_CONNECTIONS = 1000;

        public static final int DEFAULT_MAX_CONNECTION_IDLE_TTL_SECS = 90;

        private Endpoint endpoint;

        private Integer connectTimeoutSeconds;

        private Integer readWriteTimeoutSeconds;

        private Integer maxInMemoryBufferMb;

        private Integer maxConnections;

        private Integer maxConnectionIdleSeconds;

        private Boolean enableMicrometerMetric;

        private BasicHttpClientConfig fallbackConfig;

        @Override
        public Integer getMaxConnections() {
            return getWithFallback(BasicHttpClientConfig::getMaxConnections,
                    this.maxConnections, DEFAULT_MAX_CONNECTIONS, fallbackConfig);
        }

        public void setMaxConnections(Integer maxConnections) {
            this.maxConnections = maxConnections;
        }

        @Override
        public Integer getMaxConnectionIdleSeconds() {
            return getWithFallback(BasicHttpClientConfig::getMaxConnectionIdleSeconds,
                    this.maxConnectionIdleSeconds, DEFAULT_MAX_CONNECTION_IDLE_TTL_SECS, fallbackConfig);
        }

        public void setMaxConnectionIdleSeconds(Integer maxConnectionIdleSeconds) {
            this.maxConnectionIdleSeconds = maxConnectionIdleSeconds;
        }

        @Override
        public Optional<Endpoint> getEndpoint() {
            Optional<Endpoint> fallbackValue = Optional.ofNullable(fallbackConfig).flatMap(BasicHttpClientConfig::getEndpoint);
            if (fallbackValue.isPresent()) {
                return fallbackValue.map(v -> Optional.ofNullable(this.endpoint).orElse(v));
            }
            return Optional.ofNullable(this.endpoint);
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = new Endpoint(endpoint);
        }

        @Override
        public Integer getConnectTimeoutSeconds() {
            return getWithFallback(BasicHttpClientConfig::getConnectTimeoutSeconds,
                    this.connectTimeoutSeconds, DEFAULT_CONNECT_TIMEOUT_SECS, fallbackConfig);
        }

        public void setConnectTimeoutSeconds(Integer connectTimeoutSeconds) {
            this.connectTimeoutSeconds = connectTimeoutSeconds;
        }

        @Override
        public Integer getReadWriteTimeoutSeconds() {
            return getWithFallback(BasicHttpClientConfig::getReadWriteTimeoutSeconds,
                    this.readWriteTimeoutSeconds, DEFAULT_READ_TIMEOUT_SECS, fallbackConfig);
        }

        public void setReadWriteTimeoutSeconds(Integer readWriteTimeoutSeconds) {
            this.readWriteTimeoutSeconds = readWriteTimeoutSeconds;
        }

        @Override
        public Integer getMaxInMemoryBufferMb() {
            return getWithFallback(BasicHttpClientConfig::getMaxInMemoryBufferMb,
                    this.maxInMemoryBufferMb, DEFAULT_MAX_IN_MEMORY_BUFFER_MB, fallbackConfig);
        }

        public void setMaxInMemoryBufferMb(Integer maxInMemoryBufferMb) {
            this.maxInMemoryBufferMb = maxInMemoryBufferMb;
        }

        @Override
        public Boolean getEnableMicrometerMetric() {
            return getWithFallback(BasicHttpClientConfig::getEnableMicrometerMetric,
                    this.enableMicrometerMetric, false, fallbackConfig);
        }

        public void setEnableMicrometerMetric(Boolean enableMicrometerMetric) {
            this.enableMicrometerMetric = enableMicrometerMetric;
        }

        protected <X, T> T getWithFallback(Function<X, T> fallback, T primaryValue, T defaultValue, X config) {
            Optional<T> mayBeFallback = Optional.ofNullable(config)
                    .flatMap(f -> Optional.ofNullable(fallback.apply(f)));

            if (mayBeFallback.isPresent()) {
                return mayBeFallback
                        .map(fallbackValue -> Optional.ofNullable(primaryValue).orElse(fallbackValue))
                        .orElse(defaultValue);
            }

            return Optional.ofNullable(primaryValue).orElse(defaultValue);
        }

        protected void setParent(BasicHttpClientConfig fallbackConfig) {
            this.fallbackConfig = fallbackConfig;
        }

    }

}
