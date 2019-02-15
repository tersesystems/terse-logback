package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContextLoggerFactory;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;

public abstract class AbstractLogstashContextLoggerFactory<C extends Context<LogstashMarker, C>> extends AbstractContextLoggerFactory<LogstashMarker, C> {

    protected AbstractLogstashContextLoggerFactory(C context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }
}
