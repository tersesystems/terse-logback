package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class ProxyContextLoggerFactory<T extends Marker> implements ILoggerFactory {

    private final Context<T> context;
    private final ILoggerFactory loggerFactory;

    private ProxyContextLoggerFactory(Context<T> context, ILoggerFactory loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public Context<T> getContext() {
        return context;
    }

    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }

    public static <T extends Marker> ILoggerFactory create(Context<T> context, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory<T>(context, loggerFactory);
    }

    public static <T extends Marker> ILoggerFactory create(Context<T> context) {
        return create(context, LoggerFactory.getILoggerFactory());
    }

    @Override
    public Logger getLogger(String name) {
        return new ProxyContextLogger<T>(context, loggerFactory.getLogger(name));
    }
}
