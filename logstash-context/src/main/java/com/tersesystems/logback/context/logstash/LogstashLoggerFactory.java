package com.tersesystems.logback.context.logstash;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * This class is a context aware logger factory backed by a LogstashLogger.
 */
public class LogstashLoggerFactory extends AbstractLogstashLoggerFactory<LogstashContext, LogstashLogger, ILoggerFactory, LogstashLoggerFactory> {

    protected LogstashLoggerFactory(LogstashContext context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }

    @Override
    public LogstashLoggerFactory withContext(LogstashContext context) {
        return new LogstashLoggerFactory(getContext().and(context), getILoggerFactory());
    }

    @Override
    public LogstashLogger getLogger(String name) {
        return new LogstashLogger(getContext(), getILoggerFactory().getLogger(name));
    }

    public static LogstashLoggerFactory create(LogstashContext context, ILoggerFactory loggerFactory) {
        return new LogstashLoggerFactory(context, loggerFactory);
    }

    public static LogstashLoggerFactory create(ILoggerFactory iLoggerFactory) {
        return new LogstashLoggerFactory(LogstashContext.create(), iLoggerFactory);
    }

    public static LogstashLoggerFactory create(LogstashContext context) {
        return create(context, LoggerFactory.getILoggerFactory());
    }

    public static LogstashLoggerFactory create() {
        return create(LoggerFactory.getILoggerFactory());
    }

}
