package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public abstract class AbstractContextLoggerFactory<M extends Marker, C extends Context<M, C>> implements ILoggerFactory {

    protected final C context;
    protected final ILoggerFactory loggerFactory;

    protected AbstractContextLoggerFactory(C context, ILoggerFactory loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public C getContext() {
        return context;
    }

    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }
}
