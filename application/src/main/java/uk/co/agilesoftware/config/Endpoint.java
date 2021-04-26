package uk.co.agilesoftware.config;

import io.vavr.control.Try;
import lombok.ToString;

import java.net.URL;
import java.util.Optional;

@ToString
public class Endpoint {

    private final URL url;

    public Endpoint(String endpoint) {
        this.url = Try.of(() -> new URL(endpoint)).getOrElseThrow(ex -> new RuntimeException("Invalid endpoint: " + endpoint, ex));
    }

    public String getHost() {
        return url.getHost();
    }

    public String getScheme() {
        return url.getProtocol();
    }

    public String getHostAndPort() {
        return getHost() + ":" + getPort();
    }

    public Integer getPort() {
        return Optional.of(url.getPort())
                .filter(Endpoint::isValidPort)
                .orElseGet(this::getSchemeBasedPort);
    }

    private static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }

    private Integer getSchemeBasedPort() {
        if ("https".equals(getScheme())) {
            return 443;
        } else if ("http".equals(getScheme())) {
            return 80;
        }
        return -1;
    }

    public String getFile() {
        return url.getFile();
    }

    public String getUrlString() {
        return url.toString();
    }

}
