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
 * @param <C> the context type. to be used by the logger.
 * @param <PL> parent logger type.
 * @param <THIS> The self type.
 */
public abstract class AbstractLogstashLogger<C extends Context<LogstashMarker, C>, PL extends Logger, THIS> extends AbstractContextLogger<LogstashMarker, C, PL, THIS> implements LogbackLoggerAware {

    public AbstractLogstashLogger(C context, PL logger) {
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
