package com.tersesystems.logback.context;

import ch.qos.logback.classic.Logger;

import java.util.Optional;

public interface LogbackLoggerAware {
    Optional<Logger> getLogbackLogger();
}
