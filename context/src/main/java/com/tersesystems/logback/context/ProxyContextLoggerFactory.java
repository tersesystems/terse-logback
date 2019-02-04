package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProxyContextLoggerFactory implements ILoggerFactory {

    private final Context context;
    private final ILoggerFactory loggerFactory;

    public ProxyContextLoggerFactory(Context context, ILoggerFactory loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public Context getContext() {
        return context;
    }

    public ILoggerFactory getILoggerFactory() {
        return loggerFactory;
    }

    public static ILoggerFactory create(Map<?, ?> entries, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory(ContextImpl.create(entries), loggerFactory);
    }

    public static ILoggerFactory create(Context context, ILoggerFactory loggerFactory) {
        return new ProxyContextLoggerFactory(context, loggerFactory);
    }

    public static ILoggerFactory create() {
        return create(ContextImpl.create(), LoggerFactory.getILoggerFactory());
    }

    public static ILoggerFactory create(Context context) {
        return create(context, LoggerFactory.getILoggerFactory());
    }

    @Override
    public Logger getLogger(String name) {
        return new ProxyContextLogger(context, loggerFactory.getLogger(name));
    }
}
