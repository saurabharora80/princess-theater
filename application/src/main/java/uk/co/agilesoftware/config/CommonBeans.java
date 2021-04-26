package uk.co.agilesoftware.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.text.SimpleDateFormat;

@Configuration
public class CommonBeans {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    // ObjectMapper settings configured to follow content api
    public static final ObjectMapper mapper = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat(DATE_FORMAT))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            .registerModule(new VavrModule());

    public static final ObjectMapper mapperPrettyPrint = mapper.copy().enable(SerializationFeature.INDENT_OUTPUT);

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return mapperPrettyPrint;
    }

    @Bean
    WebFluxConfigurer webFluxConfigurer() {
        return new WebFluxConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry
                        .addResourceHandler("/api-doc/schema/**")
                        .addResourceLocations("classpath:/templates/api-doc/schema/");
            }
        };
    }

}
