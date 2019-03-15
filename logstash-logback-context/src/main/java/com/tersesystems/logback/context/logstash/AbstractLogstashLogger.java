/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContextLogger;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Optional;

/**
 * Ease of use abstract class for loggers which are known to use LogstashMarker explicitly.
 *
 * @param <ContextT> the context type. to be used by the logger.
 * @param <LoggerT> parent logger type.
 * @param <SelfT> The self type.
 */
public abstract class AbstractLogstashLogger<ContextT extends Context<LogstashMarker, ContextT>, LoggerT extends Logger, SelfT> extends AbstractContextLogger<LogstashMarker, ContextT, LoggerT, SelfT> implements LogbackLoggerAware {

    public AbstractLogstashLogger(ContextT context, LoggerT logger) {
        super(context, logger);
    }

    public Optional<ch.qos.logback.classic.Logger> getLogbackLogger() {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
            return Optional.of(logbackLogger);
        }

        if (logger instanceof LogbackLoggerAware) {
            return ((LogbackLoggerAware)logger).getLogbackLogger();
        }

        return Optional.empty();
    }

    @Override
    protected Marker merge(Marker marker) {
        LogstashMarker contextMarker = context.asMarker();
        if (contextMarker != null) {
            return contextMarker.and(marker);
        } else {
            return marker;
        }
    }
}
