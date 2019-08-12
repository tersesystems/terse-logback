package com.tersesystems.logback.bytebuddy;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Objects;

public class DeclaringTypeLoggerResolver implements LoggerResolver {

    private final ILoggerFactory loggerFactory;

    public DeclaringTypeLoggerResolver(ILoggerFactory loggerFactory) {
        this.loggerFactory = Objects.requireNonNull(loggerFactory);
    }

    @Override
    public Logger resolve(String origin) {
        int firstPipe = origin.indexOf('|');
        String declaringType = origin.substring(0, firstPipe);
        return loggerFactory.getLogger(declaringType);
    }
}
