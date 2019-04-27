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
package com.tersesystems.logback.ext.proxy;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 *
 */
public interface ProxyLogger extends Logger {

    Logger logger();

    @Override
    default String getName() {
        return logger().getName();
    }

    @Override
    default boolean isTraceEnabled() {
        return logger().isTraceEnabled();
    }

    @Override
    default void trace(String msg) {
        logger().trace(msg);
    }

    @Override
    default void trace(String format, Object arg) {
        logger().trace(format, arg);
    }

    @Override
    default void trace(String format, Object arg1, Object arg2) {
        logger().trace(format, arg1, arg2);
    }

    @Override
    default void trace(String format, Object... arguments) {
        logger().trace(format, arguments);
    }

    @Override
    default void trace(String msg, Throwable t) {
        logger().trace(msg, t);
    }

    @Override
    default boolean isTraceEnabled(Marker marker) {
        return logger().isTraceEnabled(marker);
    }

    @Override
    default void trace(Marker marker, String msg) {
        logger().trace(marker, msg);
    }

    @Override
    default void trace(Marker marker, String format, Object arg) {
        logger().trace(marker, format, arg);
    }

    @Override
    default void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger().trace(marker, format, arg1, arg2);
    }

    @Override
    default void trace(Marker marker, String format, Object... argArray) {
        logger().trace(marker, format, argArray);
    }

    @Override
    default void trace(Marker marker, String msg, Throwable t) {
        logger().trace(marker, msg, t);
    }


    @Override
    default boolean isDebugEnabled() {
        return logger().isDebugEnabled();
    }

    @Override
    default void debug(String msg) {
        logger().debug(msg);
    }

    @Override
    default void debug(String format, Object arg) {
        logger().debug(format, arg);
    }

    @Override
    default void debug(String format, Object arg1, Object arg2) {
        logger().debug(format, arg1, arg2);
    }

    @Override
    default void debug(String format, Object... arguments) {
        logger().debug(format, arguments);
    }

    @Override
    default void debug(String msg, Throwable t) {
        logger().debug(msg, t);
    }

    @Override
    default boolean isDebugEnabled(Marker marker) {
        return logger().isDebugEnabled(marker);
    }

    @Override
    default void debug(Marker marker, String msg) {
        logger().debug(marker, msg);
    }

    @Override
    default void debug(Marker marker, String format, Object arg) {
        logger().debug(marker, format, arg);
    }

    @Override
    default void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger().debug(marker, format, arg1, arg2);
    }

    @Override
    default void debug(Marker marker, String format, Object... argArray) {
        logger().debug(marker, format, argArray);
    }

    @Override
    default void debug(Marker marker, String msg, Throwable t) {
        logger().debug(marker, msg, t);
    }


    @Override
    default boolean isInfoEnabled() {
        return logger().isInfoEnabled();
    }

    @Override
    default void info(String msg) {
        logger().info(msg);
    }

    @Override
    default void info(String format, Object arg) {
        logger().info(format, arg);
    }

    @Override
    default void info(String format, Object arg1, Object arg2) {
        logger().info(format, arg1, arg2);
    }

    @Override
    default void info(String format, Object... arguments) {
        logger().info(format, arguments);
    }

    @Override
    default void info(String msg, Throwable t) {
        logger().info(msg, t);
    }

    @Override
    default boolean isInfoEnabled(Marker marker) {
        return logger().isInfoEnabled(marker);
    }

    @Override
    default void info(Marker marker, String msg) {
        logger().info(marker, msg);
    }

    @Override
    default void info(Marker marker, String format, Object arg) {
        logger().info(marker, format, arg);
    }

    @Override
    default void info(Marker marker, String format, Object arg1, Object arg2) {
        logger().info(marker, format, arg1, arg2);
    }

    @Override
    default void info(Marker marker, String format, Object... argArray) {
        logger().info(marker, format, argArray);
    }

    @Override
    default void info(Marker marker, String msg, Throwable t) {
        logger().info(marker, msg, t);
    }


    @Override
    default boolean isWarnEnabled() {
        return logger().isWarnEnabled();
    }

    @Override
    default void warn(String msg) {
        logger().warn(msg);
    }

    @Override
    default void warn(String format, Object arg) {
        logger().warn(format, arg);
    }

    @Override
    default void warn(String format, Object arg1, Object arg2) {
        logger().warn(format, arg1, arg2);
    }

    @Override
    default void warn(String format, Object... arguments) {
        logger().warn(format, arguments);
    }

    @Override
    default void warn(String msg, Throwable t) {
        logger().warn(msg, t);
    }

    @Override
    default boolean isWarnEnabled(Marker marker) {
        return logger().isWarnEnabled(marker);
    }

    @Override
    default void warn(Marker marker, String msg) {
        logger().warn(marker, msg);
    }

    @Override
    default void warn(Marker marker, String format, Object arg) {
        logger().warn(marker, format, arg);
    }

    @Override
    default void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger().warn(marker, format, arg1, arg2);
    }

    @Override
    default void warn(Marker marker, String format, Object... argArray) {
        logger().warn(marker, format, argArray);
    }

    @Override
    default void warn(Marker marker, String msg, Throwable t) {
        logger().warn(marker, msg, t);
    }


    @Override
    default boolean isErrorEnabled() {
        return logger().isErrorEnabled();
    }

    @Override
    default void error(String msg) {
        logger().error(msg);
    }

    @Override
    default void error(String format, Object arg) {
        logger().error(format, arg);
    }

    @Override
    default void error(String format, Object arg1, Object arg2) {
        logger().error(format, arg1, arg2);
    }

    @Override
    default void error(String format, Object... arguments) {
        logger().error(format, arguments);
    }

    @Override
    default void error(String msg, Throwable t) {
        logger().error(msg, t);
    }

    @Override
    default boolean isErrorEnabled(Marker marker) {
        return logger().isErrorEnabled(marker);
    }

    @Override
    default void error(Marker marker, String msg) {
        logger().error(marker, msg);
    }

    @Override
    default void error(Marker marker, String format, Object arg) {
        logger().error(marker, format, arg);
    }

    @Override
    default void error(Marker marker, String format, Object arg1, Object arg2) {
        logger().error(marker, format, arg1, arg2);
    }

    @Override
    default void error(Marker marker, String format, Object... argArray) {
        logger().error(marker, format, argArray);
    }

    @Override
    default void error(Marker marker, String msg, Throwable t) {
        logger().error(marker, msg, t);
    }

}
