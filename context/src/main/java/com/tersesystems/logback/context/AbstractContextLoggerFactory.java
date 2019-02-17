package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class AbstractContextLoggerFactory<M extends Marker, C extends Context<M, C>, L extends Logger, IF extends ILoggerFactory> implements ISelfLoggerFactory<L> {

    protected final C context;
    protected final IF loggerFactory;

    protected AbstractContextLoggerFactory(C context, IF loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public C getContext() {
        return context;
    }

    public IF getILoggerFactory() {
        return loggerFactory;
    }
}
