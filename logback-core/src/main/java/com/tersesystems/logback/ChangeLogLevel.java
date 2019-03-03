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
package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Provide a way to change the logging level dynamically in Logback.
 */
public class ChangeLogLevel {

    private final ILoggerFactory loggerFactory;

    public ChangeLogLevel() {
        this(LoggerFactory.getILoggerFactory());
    }

    public ChangeLogLevel(ILoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    public void changeLogLevel(String loggerName, String levelName) {
        changeLogLevel(loggerFactory.getLogger(loggerName), levelName);
    }

    public final void changeLogLevel(String loggerName, int levelNumber) {
        changeLogLevel(loggerFactory.getLogger(loggerName), levelNumber);
    }

    public final void changeLogLevel(org.slf4j.Logger logger, String levelName) {
        Logger logbackLogger = (Logger) logger;
        Level level = Level.toLevel(levelName);
        logbackLogger.setLevel(level);
    }

    public final void changeLogLevel(org.slf4j.Logger logger, int levelNumber) {
        Logger logbackLogger = (Logger) logger;
        Level level = Level.toLevel(levelNumber);
        logbackLogger.setLevel(level);
    }

}
