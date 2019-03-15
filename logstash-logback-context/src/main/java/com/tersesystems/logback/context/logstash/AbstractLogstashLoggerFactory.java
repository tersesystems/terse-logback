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

import com.tersesystems.logback.context.AbstractContextLoggerFactory;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Helper class that fixes the marker as LogstashMarker and adds a withContext method.
 *
 * @param <ContextT> the context type.
 * @param <LoggerT> the logger type.
 * @param <LoggerFactoryT> the parent loggerfactory type.
 * @param <SelfT> the self type for the logger factory.
 */
public abstract class AbstractLogstashLoggerFactory<
        ContextT extends Context<LogstashMarker, ContextT>,
        LoggerT extends Logger,
        LoggerFactoryT extends ILoggerFactory,
        SelfT
  > extends AbstractContextLoggerFactory<LogstashMarker, ContextT, LoggerT, LoggerFactoryT> {

    protected AbstractLogstashLoggerFactory(ContextT context, LoggerFactoryT loggerFactory) {
        super(context, loggerFactory);
    }

    public abstract SelfT withContext(ContextT context);
}
