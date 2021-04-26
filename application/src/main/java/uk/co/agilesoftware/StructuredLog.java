package uk.co.agilesoftware;

import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class StructuredLog {
    private final Logger log;
    private final StopWatch stopWatch;

    public StructuredLog(Logger log) {
        this(log, new StopWatch());
    }

    public StructuredLog(Logger log, StopWatch stopWatch) {
        this.log = log;
        stopWatch.start();
        this.stopWatch = stopWatch;
    }

    private final List<StructuredArgument> structuredArguments = new ArrayList<>();

    public StructuredLog with(String key, Object value) {
        structuredArguments.add(StructuredArguments.keyValue(key, value));
        return this;
    }

    public void error(String message) {
        log(log::error, message);
    }

    public void debug(String message) {
        log(log::debug, message);
    }

    private void log(final LogFunction function, final String message, Object... args) {
        structuredArguments.add(StructuredArguments.keyValue("elapsedTimeMs", stopWatch.getTotalTimeMillis()));
        function.log(message, structuredArguments.toArray());
    }

    public void warn(String message) {
        log(log::warn, message);
    }

    public void info(String message) {
        log(log::info, message);
    }

    @FunctionalInterface
    private interface LogFunction {
        void log(String message, Object... args);
    }
}
