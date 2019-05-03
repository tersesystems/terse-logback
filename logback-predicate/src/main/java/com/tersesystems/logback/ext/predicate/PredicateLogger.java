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
package com.tersesystems.logback.ext.predicate;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import java.util.function.Predicate;

public interface PredicateLogger extends Logger {

    Predicate<Level> predicate();

    Logger logger();

    @Override
    default String getName() {
        return logger().getName();
    }

    @Override
    default boolean isTraceEnabled() {
        return logger().isTraceEnabled() && predicate().test(Level.TRACE);
    }

    @Override
    default void trace(String msg) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(msg);
        }
    }

    @Override
    default void trace(String format, Object arg) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(format, arg);
        }
    }

    @Override
    default void trace(String format, Object arg1, Object arg2) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(format, arg1, arg2);
        }
    }

    @Override
    default void trace(String format, Object... arguments) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(format, arguments);
        }
    }

    @Override
    default void trace(String msg, Throwable t) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(msg, t);
        }
    }

    @Override
    default boolean isTraceEnabled(Marker marker) {
        return logger().isTraceEnabled(marker) && predicate().test(Level.TRACE);
    }

    @Override
    default void trace(Marker marker, String msg) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(marker, msg);
        }
    }

    @Override
    default void trace(Marker marker, String format, Object arg) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(marker, format, arg);
        }
    }

    @Override
    default void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(marker, format, arg1, arg2);
        }
    }

    @Override
    default void trace(Marker marker, String format, Object... argArray) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(marker, format, argArray);
        }
    }

    @Override
    default void trace(Marker marker, String msg, Throwable t) {
        if (predicate().test(Level.TRACE)) {
            logger().trace(marker, msg, t);
        }
    }

    @Override
    default boolean isDebugEnabled() {
        return logger().isDebugEnabled() && predicate().test(Level.DEBUG);
    }

    @Override
    default void debug(String msg) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(msg);
        }
    }

    @Override
    default void debug(String format, Object arg) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(format, arg);
        }
    }

    @Override
    default void debug(String format, Object arg1, Object arg2) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(format, arg1, arg2);
        }
    }

    @Override
    default void debug(String format, Object... arguments) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(format, arguments);
        }
    }

    @Override
    default void debug(String msg, Throwable t) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(msg, t);
        }
    }

    @Override
    default boolean isDebugEnabled(Marker marker) {
        return logger().isDebugEnabled(marker) && predicate().test(Level.DEBUG);
    }

    @Override
    default void debug(Marker marker, String msg) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(marker, msg);
        }
    }

    @Override
    default void debug(Marker marker, String format, Object arg) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(marker, format, arg);
        }
    }

    @Override
    default void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(marker, format, arg1, arg2);
        }
    }

    @Override
    default void debug(Marker marker, String format, Object... argArray) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(marker, format, argArray);
        }
    }

    @Override
    default void debug(Marker marker, String msg, Throwable t) {
        if (predicate().test(Level.DEBUG)) {
            logger().debug(marker, msg, t);
        }
    }

    @Override
    default boolean isInfoEnabled() {
        return logger().isInfoEnabled() && predicate().test(Level.INFO);
    }

    @Override
    default void info(String msg) {
        if (predicate().test(Level.INFO)) {
            logger().info(msg);
        }
    }

    @Override
    default void info(String format, Object arg) {
        if (predicate().test(Level.INFO)) {
            logger().info(format, arg);
        }
    }

    @Override
    default void info(String format, Object arg1, Object arg2) {
        if (predicate().test(Level.INFO)) {
            logger().info(format, arg1, arg2);
        }
    }

    @Override
    default void info(String format, Object... arguments) {
        if (predicate().test(Level.INFO)) {
            logger().info(format, arguments);
        }
    }

    @Override
    default void info(String msg, Throwable t) {
        if (predicate().test(Level.INFO)) {
            logger().info(msg, t);
        }
    }

    @Override
    default boolean isInfoEnabled(Marker marker) {
        return logger().isInfoEnabled(marker) && predicate().test(Level.INFO);
    }

    @Override
    default void info(Marker marker, String msg) {
        if (predicate().test(Level.INFO)) {
            logger().info(marker, msg);
        }
    }

    @Override
    default void info(Marker marker, String format, Object arg) {
        if (predicate().test(Level.INFO)) {
            logger().info(marker, format, arg);
        }
    }

    @Override
    default void info(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate().test(Level.INFO)) {
            logger().info(marker, format, arg1, arg2);
        }
    }

    @Override
    default void info(Marker marker, String format, Object... argArray) {
        if (predicate().test(Level.INFO)) {
            logger().info(marker, format, argArray);
        }
    }

    @Override
    default void info(Marker marker, String msg, Throwable t) {
        if (predicate().test(Level.INFO)) {
            logger().info(marker, msg, t);
        }
    }

    @Override
    default boolean isWarnEnabled() {
        return logger().isWarnEnabled() && predicate().test(Level.WARN);
    }

    @Override
    default void warn(String msg) {
        if (predicate().test(Level.WARN)) {
            logger().warn(msg);
        }
    }

    @Override
    default void warn(String format, Object arg) {
        if (predicate().test(Level.WARN)) {
            logger().warn(format, arg);
        }
    }

    @Override
    default void warn(String format, Object arg1, Object arg2) {
        if (predicate().test(Level.WARN)) {
            logger().warn(format, arg1, arg2);
        }
    }

    @Override
    default void warn(String format, Object... arguments) {
        if (predicate().test(Level.WARN)) {
            logger().warn(format, arguments);
        }
    }

    @Override
    default void warn(String msg, Throwable t) {
        if (predicate().test(Level.WARN)) {
            logger().warn(msg, t);
        }
    }

    @Override
    default boolean isWarnEnabled(Marker marker) {
        return logger().isWarnEnabled(marker) && predicate().test(Level.WARN);
    }

    @Override
    default void warn(Marker marker, String msg) {
        if (predicate().test(Level.WARN)) {
            logger().warn(marker, msg);
        }
    }

    @Override
    default void warn(Marker marker, String format, Object arg) {
        if (predicate().test(Level.WARN)) {
            logger().warn(marker, format, arg);
        }
    }

    @Override
    default void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate().test(Level.WARN)) {
            logger().warn(marker, format, arg1, arg2);
        }
    }

    @Override
    default void warn(Marker marker, String format, Object... argArray) {
        if (predicate().test(Level.WARN)) {
            logger().warn(marker, format, argArray);
        }
    }

    @Override
    default void warn(Marker marker, String msg, Throwable t) {
        if (predicate().test(Level.WARN)) {
            logger().warn(marker, msg, t);
        }
    }

    @Override
    default boolean isErrorEnabled() {
        return logger().isErrorEnabled() && predicate().test(Level.ERROR);
    }

    @Override
    default void error(String msg) {
        if (predicate().test(Level.ERROR)) {
            logger().error(msg);
        }
    }

    @Override
    default void error(String format, Object arg) {
        if (predicate().test(Level.ERROR)) {
            logger().error(format, arg);
        }
    }

    @Override
    default void error(String format, Object arg1, Object arg2) {
        if (predicate().test(Level.ERROR)) {
            logger().error(format, arg1, arg2);
        }
    }

    @Override
    default void error(String format, Object... arguments) {
        if (predicate().test(Level.ERROR)) {
            logger().error(format, arguments);
        }
    }

    @Override
    default void error(String msg, Throwable t) {
        if (predicate().test(Level.ERROR)) {
            logger().error(msg, t);
        }
    }

    @Override
    default boolean isErrorEnabled(Marker marker) {
        return logger().isErrorEnabled(marker) && predicate().test(Level.ERROR);
    }

    @Override
    default void error(Marker marker, String msg) {
        if (predicate().test(Level.ERROR)) {
            logger().error(marker, msg);
        }
    }

    @Override
    default void error(Marker marker, String format, Object arg) {
        if (predicate().test(Level.ERROR)) {
            logger().error(marker, format, arg);
        }
    }

    @Override
    default void error(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate().test(Level.ERROR)) {
            logger().error(marker, format, arg1, arg2);
        }
    }

    @Override
    default void error(Marker marker, String format, Object... argArray) {
        if (predicate().test(Level.ERROR)) {
            logger().error(marker, format, argArray);
        }
    }

    @Override
    default void error(Marker marker, String msg, Throwable t) {
        if (predicate().test(Level.ERROR)) {
            logger().error(marker, msg, t);
        }
    }
}
