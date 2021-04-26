package uk.co.agilesoftware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"uk.co.agilesoftware"})
public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    private final Environment environment;

    private final BuildProperties buildProperties;

    private final GitProperties gitProperties;

    public Application(
            Environment environment,
            @Autowired(required = false) BuildProperties buildProperties,
            @Autowired(required = false) GitProperties gitProperties
    ) {
        this.environment = Objects.requireNonNull(environment);
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        String format = "%n" +
                "--------------------------------------------------------------------------------------%n" +
                "    Application %2$s v%8$s (%9$s) is running! Access URLs:%n" +
                "    Local               : %1$s://localhost:%3$s%5$s%n" +
                "    ApiDoc              : %1$s://localhost:%3$s%5$s/api-doc/index.html %n" +
                "    Management          : %1$s://localhost:%4$s%5$s/actuator%n" +
                "    Available Resources : %1$s://localhost:%4$s%5$s/actuator/mappings%n" +
                "    External            : %1$s://%6$s:%4$s%5$s%n" +
                "    Profile(s)          : %7$s%n" +
                "--------------------------------------------------------------------------------------";

        String protocol = Optional.ofNullable(environment.getProperty("server.ssl.key-store"))
                .map(v -> "https")
                .orElse("http");

        String name = mayBeBuildProperties().flatMap(b -> Optional.of(b.getName())).orElse("NoName");

        String serverPort = Optional.ofNullable(environment.getProperty("server.port")).orElse("8080");

        String managementPort = Optional.ofNullable(environment.getProperty("management.server.port")).orElse(serverPort);

        String contextPath = Optional.ofNullable(environment.getProperty("server.servlet.context-path")).orElse("");

        String activeProfiles = Arrays.toString(environment.getActiveProfiles());

        String version = mayBeBuildProperties().flatMap(b -> Optional.ofNullable(b.getVersion())).orElse("NoVersion");

        String commitId = mayBeGitProperties().flatMap(g -> Optional.ofNullable(g.getShortCommitId())).orElse("NoCommitId");

        log.info(String.format(
                format,
                protocol,
                name,
                serverPort,
                managementPort,
                contextPath,
                host(),
                activeProfiles,
                version,
                commitId
        ));
    }

    private String host() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Throwable ex) {
            return "localhost";
        }
    }

    private Optional<GitProperties> mayBeGitProperties() {
        return Optional.ofNullable(gitProperties);
    }

    private Optional<BuildProperties> mayBeBuildProperties() {
        return Optional.ofNullable(buildProperties);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
