/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
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
