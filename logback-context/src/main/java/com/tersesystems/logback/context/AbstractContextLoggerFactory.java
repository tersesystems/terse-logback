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
