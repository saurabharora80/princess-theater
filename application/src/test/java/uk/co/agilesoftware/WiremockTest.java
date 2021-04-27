package uk.co.agilesoftware;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class WiremockTest {
    private static WireMockServer wireMockServer = new WireMockServer(9090);

    @BeforeAll
    public static void startWiremock() {
        WireMock.configureFor(9090);
        wireMockServer.start();
    }

    @BeforeEach
    public void resetWiremock() {
        wireMockServer.start();
    }

    @AfterAll
    public static void stopWiremock() {
        wireMockServer.stop();
    }

}
