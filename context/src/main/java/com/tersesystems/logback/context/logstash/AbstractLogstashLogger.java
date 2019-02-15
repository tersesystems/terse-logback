package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContextLogger;
import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.LogbackLoggerAware;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Ease of use abstract class for loggers which are known to use LogstashMarker explicitly.
 *
 * @param <C>
 * @param <THIS>
 */
public abstract class AbstractLogstashLogger<C extends Context<LogstashMarker, C>, THIS> extends AbstractContextLogger<LogstashMarker, C, THIS> implements LogbackLoggerAware {

    public AbstractLogstashLogger(C context, Logger logger) {
        super(context, logger);
    }

    public Optional<ch.qos.logback.classic.Logger> getLogbackLogger() {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
            return Optional.of(logbackLogger);
        }

        if (logger instanceof LogbackLoggerAware){
            return ((LogbackLoggerAware)logger).getLogbackLogger();
        }

        return Optional.empty();
    }
}
