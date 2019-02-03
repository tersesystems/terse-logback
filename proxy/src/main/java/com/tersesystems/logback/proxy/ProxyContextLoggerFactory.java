package com.tersesystems.logback.proxy;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class ProxyContextLoggerFactory implements ILoggerFactory {

    private final MarkerContext markerContext;
    private final ILoggerFactory loggerFactory;

    public ProxyContextLoggerFactory(MarkerContext markerContext, ILoggerFactory loggerFactory) {
        this.markerContext = markerContext;
        this.loggerFactory = loggerFactory;
    }

    public MarkerContext getMarkerContext() {
        return markerContext;
    }

    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }

    public static ILoggerFactory create(Marker marker, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory(LogstashMarkerContext.create(marker), loggerFactory);
    }

    public static ILoggerFactory create(MarkerContext markerContext, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory(markerContext, loggerFactory);
    }

    public static ILoggerFactory create(Marker marker) {
        return create(marker, LoggerFactory.getILoggerFactory());
    }

    public static ILoggerFactory create(MarkerContext markerContext) {
        return create(markerContext, LoggerFactory.getILoggerFactory());
    }

    @Override
    public Logger getLogger(String name) {
        return new ProxyContextLogger(markerContext, loggerFactory.getLogger(name));
    }
}
