package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContextLoggerFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class LogstashLoggerFactory extends AbstractContextLoggerFactory<LogstashMarker, LogstashContext> {

    protected LogstashLoggerFactory(LogstashContext context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }

    public static LogstashLoggerFactory create(LogstashContext context, ILoggerFactory loggerFactory) {
        return new LogstashLoggerFactory(context, loggerFactory);
    }

    private static LogstashLoggerFactory create(ILoggerFactory iLoggerFactory) {
        return new LogstashLoggerFactory(LogstashContext.create(), iLoggerFactory);
    }

    public static LogstashLoggerFactory create(LogstashContext context) {
        return create(context, LoggerFactory.getILoggerFactory());
    }

    public static LogstashLoggerFactory create() {
        return create(LoggerFactory.getILoggerFactory());
    }

    @Override
    public LogstashLogger getLogger(String name) {
        return new LogstashLogger(getContext(), getILoggerFactory().getLogger(name));
    }

    // Add back the chrome
    public LogstashLogger getLogger(Class<?> clazz) {
        return new LogstashLogger(getContext(), getILoggerFactory().getLogger(clazz.getName()));
    }
}
