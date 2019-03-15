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
package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class AbstractContextLoggerFactory<
        MarkerT extends Marker,
        ContextT extends Context<MarkerT, ContextT>,
        LoggerT extends Logger,
        LoggerFactoryT extends ILoggerFactory
        > implements ISelfLoggerFactory<LoggerT> {

    protected final ContextT context;
    protected final LoggerFactoryT loggerFactory;

    protected AbstractContextLoggerFactory(ContextT context, LoggerFactoryT loggerFactory) {
        this.context = context;
        this.loggerFactory = loggerFactory;
    }

    public ContextT getContext() {
        return context;
    }

    public LoggerFactoryT getILoggerFactory() {
        return loggerFactory;
    }
}
