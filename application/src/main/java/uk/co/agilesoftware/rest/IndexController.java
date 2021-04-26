package uk.co.agilesoftware.rest;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Controller
public class IndexController {

    private final Environment env;

    private final BuildProperties buildProperties;

    private final GitProperties gitProperties;

    public IndexController(
            Environment env,
            @Autowired(required = false) BuildProperties buildProperties,
            @Autowired(required = false) GitProperties gitProperties
    ) {
        this.env = Objects.requireNonNull(env);
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
    }

    @GetMapping({"", "/", "/index.html"})
    public Mono<String> index() {
        return Mono.just("redirect:/api-doc/index.html");
    }

    @GetMapping(path = "/api-doc/index.html", produces = "text/html; charset=utf-8")
    public Mono<String> apidocIndex(final Model model) {
        return Mono.just("api-doc/index")
                .doOnNext(view -> model.addAttribute("contextPath", contextPath()));
    }

    private String contextPath() {
        return Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("/");
    }

    @GetMapping(path = "/api-doc/openapi.yaml", produces = "text/html; charset=utf-8")
    public Mono<String> apiDoc(final Model model) {
        return Mono.just("api-doc/openapi")
                .doOnNext(view -> model.addAttribute("contextPath", contextPath()))
                .doOnNext(view -> model.addAttribute("applicationTitle", applicationTitle()))
                .doOnNext(view -> model.addAttribute("applicationVersion", applicationVersion()))
                .doOnNext(view -> model.addAttribute("commitId", commitId()));
    }

    private String commitId() {
        if (gitProperties == null) {
            return "NoCommitId";
        } else {
            return StringUtils.defaultString(gitProperties.getShortCommitId(), "NoCommitId");
        }
    }

    private String applicationVersion() {
        if (buildProperties == null) {
            return "NoVersion";
        } else {
            return StringUtils.defaultString(buildProperties.getVersion(), "NoVersion");
        }
    }

    private String applicationTitle() {
        if (buildProperties == null) {
            return "NoName";
        } else {
            return StringUtils.defaultString(buildProperties.getName(), "NoName");
        }
    }

}
