package com.tersesystems.logback;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyContextLoggerFactory implements ILoggerFactory {

    private final LogstashMarker context;
    private final ILoggerFactory loggerFactory;

    public ProxyContextLoggerFactory(LogstashMarker context, ILoggerFactory loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public static ILoggerFactory createLoggerFactory(LogstashMarker context, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory(context, loggerFactory);
    }

    public static ILoggerFactory createLoggerFactory(LogstashMarker context) {
        return createLoggerFactory(context, LoggerFactory.getILoggerFactory());
    }

    @Override
    public Logger getLogger(String name) {
        return new ProxyContextLogger(context, loggerFactory.getLogger(name));
    }
}
