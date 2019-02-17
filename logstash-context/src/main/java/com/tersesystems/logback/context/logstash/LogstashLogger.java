package com.tersesystems.logback.context.logstash;

import org.slf4j.Logger;

/**
 * This class is a context aware logger backed by a LogstashContext.
 */
public class LogstashLogger extends AbstractLogstashLogger<LogstashContext, Logger, LogstashLogger> {

    public LogstashLogger(LogstashContext context, Logger logger) {
        super(context, logger);
    }

    @Override
    public LogstashLogger withContext(LogstashContext otherContext) {
        return new LogstashLogger(this.context.and(otherContext), this.logger);
    }
}
