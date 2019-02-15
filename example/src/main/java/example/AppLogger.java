package example;

import com.tersesystems.logback.context.logstash.AbstractLogstashContextLoggerFactory;
import com.tersesystems.logback.context.logstash.AbstractLogstashLogger;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppLogger extends AbstractLogstashLogger<AppContext, AppLogger> {

    public AppLogger(AppContext context, Logger logger) {
        super(context, logger);
    }

    @Override
    public AppLogger withContext(AppContext otherContext) {
        return new AppLogger(this.context.and(otherContext), this.logger);
    }
}

class AppLoggerFactory extends AbstractLogstashContextLoggerFactory<AppContext> {

    protected AppLoggerFactory(AppContext context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }

    public static AppLoggerFactory create() {
        return create(AppContext.create());
    }

    public static AppLoggerFactory create(AppContext context) {
        return new AppLoggerFactory(context, LoggerFactory.getILoggerFactory());
    }

    @Override
    public Logger getLogger(String name) {
        return new AppLogger(AppContext.create(), getILoggerFactory().getLogger(name));
    }

    public Logger getLogger(Class<?> clazz) {
        return new AppLogger(AppContext.create(), getILoggerFactory().getLogger(clazz.getName()));
    }
}