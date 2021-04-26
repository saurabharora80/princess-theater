package uk.co.agilesoftware.config.beans;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import uk.co.agilesoftware.StructuredLog;

@Slf4j
public class WebClientLoggingInterceptor implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        //Must be instantiated before `ExchangeFunction.exchange` to ensure correct calculation of Request/Response time
        StructuredLog structuredLog = new StructuredLog(log);

        return next.exchange(request).doOnSuccess(response -> {
            String logMessage = String.format("%s %s %s", request.method(), request.url(), response.statusCode());

            appendRequestInfo(request, structuredLog)
                    .with("responseCode", response.rawStatusCode())
                    .with("responseHeaders", response.headers());

            if (response.statusCode().is2xxSuccessful() || response.statusCode().is3xxRedirection()) {
                structuredLog.debug(logMessage);
            } else if (response.statusCode().is4xxClientError()) {
                if (response.statusCode().value() == 404) {
                    structuredLog.info(logMessage);
                } else {
                    /*
                       Upstream 4xx error should be logged as ERROR as it is Our Fault
                    */
                    structuredLog.error(logMessage);
                }
            } else {
                /*
                   Upstream 5xx error should be logged as WARN as it is Not Our Fault
                */
                structuredLog.warn(logMessage);
            }
        }).doOnError(throwable -> {
            String exceptionLogMessage = String.format("%s %s %s", request.method(), request.url(),
                    throwable.getClass().getSimpleName());
            appendRequestInfo(request, structuredLog)
                    .with("exception", throwable.getClass().getSimpleName())
                    .error(exceptionLogMessage);
        });
    }

    private StructuredLog appendRequestInfo(ClientRequest request, StructuredLog structuredLog) {
        return structuredLog
                .with("method", request.method())
                .with("url", request.url())
                .with("requestHeaders", request.headers());
    }
}
