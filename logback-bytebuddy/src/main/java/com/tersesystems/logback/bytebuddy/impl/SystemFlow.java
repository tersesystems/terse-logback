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
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class SystemFlow {
    // https://github.com/qos-ch/slf4j/blob/master/slf4j-ext/src/main/java/org/slf4j/ext/XLogger.java#L44
    public static final Marker FLOW_MARKER = MarkerFactory.getMarker("FLOW");
    public static final Marker ENTRY_MARKER = MarkerFactory.getMarker("ENTRY");
    public static final Marker EXIT_MARKER = MarkerFactory.getMarker("EXIT");
    public static final Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");

    static {
        ENTRY_MARKER.add(FLOW_MARKER);
        EXIT_MARKER.add(FLOW_MARKER);
    }

    private static LoggerResolver loggerResolver = new DeclaringTypeLoggerResolver(LoggerFactory::getILoggerFactory);

    public static LoggerResolver getLoggerResolver() {
        return loggerResolver;
    }

    public static void setLoggerResolver(LoggerResolver loggerResolver) {
        SystemFlow.loggerResolver = loggerResolver;
    }

    public static Logger getLogger(String origin) {
        return loggerResolver.resolve(origin);
    }

}
