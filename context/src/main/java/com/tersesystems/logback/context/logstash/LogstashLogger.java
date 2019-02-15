package com.tersesystems.logback.context.logstash;

import org.slf4j.Logger;

public class LogstashLogger extends AbstractLogstashLogger<LogstashContext, LogstashLogger> {

    public LogstashLogger(LogstashContext context, Logger logger) {
        super(context, logger);
    }

    @Override
    public LogstashLogger withContext(LogstashContext otherContext) {
        return new LogstashLogger(this.context.and(otherContext), this.logger);
    }
}
