package com.tersesystems.logback.context.logstash;

import java.util.Optional;

/**
 * Helper class for getting at the logback logger.
 */
public interface LogbackLoggerAware {
    Optional<ch.qos.logback.classic.Logger> getLogbackLogger();
}
