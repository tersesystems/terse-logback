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
package com.tersesystems.logback.bytebuddy;

import org.slf4j.Logger;

import java.util.Objects;

public class ThreadLocalLogger {

    private static final ThreadLocal<Logger> threadLocal = new ThreadLocal<Logger>();

    public static Logger getLogger() {
        return (threadLocal.get());
    }

    public static void setLogger(Logger logger) {
        Objects.requireNonNull(logger);
        threadLocal.set(logger);
    }

    public static void clearLogger() {
        threadLocal.set(null);
    }

}