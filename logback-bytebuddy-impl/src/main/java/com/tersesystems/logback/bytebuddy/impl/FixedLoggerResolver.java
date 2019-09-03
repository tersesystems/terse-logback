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
package com.tersesystems.logback.bytebuddy.impl;

import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Always returns the same logger.
 */
public class FixedLoggerResolver implements LoggerResolver {
    private final Logger logger;

    public FixedLoggerResolver(Logger logger) {
        this.logger = requireNonNull(logger);
    }

    @Override
    public Logger resolve(String origin) {
        return logger;
    }
}
