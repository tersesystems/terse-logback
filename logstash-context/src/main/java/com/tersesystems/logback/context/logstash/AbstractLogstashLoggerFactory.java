package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContextLoggerFactory;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Helper class that fixes the marker as LogstashMarker and adds a withContext method.
 *
 * @param <C> the context type.
 * @param <L> the logger type.
 * @param <PIF> the parent loggerfactory type.
 * @param <THIS> the self type for the logger factory.
 */
public abstract class AbstractLogstashLoggerFactory<C extends Context<LogstashMarker, C>, L extends Logger, PIF extends ILoggerFactory, THIS> extends AbstractContextLoggerFactory<LogstashMarker, C, L, PIF> {

    protected AbstractLogstashLoggerFactory(C context, PIF loggerFactory) {
        super(context, loggerFactory);
    }

    public abstract THIS withContext(C context);
}
