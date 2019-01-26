package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide a way to change the logging level dynamically in Logback.
 */
public class ChangeLogLevel {

    public static final void changeLogLevel(String loggerName, String levelName) {
        changeLogLevel(LoggerFactory.getLogger(loggerName), levelName);
    }

    public static final void changeLogLevel(String loggerName, int levelNumber) {
        changeLogLevel(LoggerFactory.getLogger(loggerName), levelNumber);
    }

    public static final void changeLogLevel(org.slf4j.Logger logger, String levelName) {
        Logger logbackLogger = (Logger) logger;
        Level level = Level.toLevel(levelName);
        logbackLogger.setLevel(level);
    }

    public static final void changeLogLevel(org.slf4j.Logger logger, int levelNumber) {
        Logger logbackLogger = (Logger) logger;
        Level level = Level.toLevel(levelNumber);
        logbackLogger.setLevel(level);
    }
}
