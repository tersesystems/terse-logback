package com.tersesystems.logback.bytebuddy;

import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

public class FixedLoggerResolver implements LoggerResolver {
    private final Logger logger;

    public FixedLoggerResolver(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    @Override
    public Logger resolve(String origin) {
        return logger;
    }
}
