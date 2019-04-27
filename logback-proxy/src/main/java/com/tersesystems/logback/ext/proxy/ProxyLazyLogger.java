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

import com.tersesystems.logback.ext.LazyLogger;
import com.tersesystems.logback.ext.LoggerStatement;
import org.slf4j.Logger;
import org.slf4j.Marker;
import java.util.function.*;
import java.util.Optional;


/**
 * A lazy logger with default interfaces.
 */
public interface ProxyLazyLogger extends LazyLogger {

    Logger logger();

    default void trace(Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> trace() {
        if (logger().isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void trace(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> trace(Marker marker) {
        if (logger().isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void debug(Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> debug() {
        if (logger().isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void debug(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> debug(Marker marker) {
        if (logger().isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void info(Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> info() {
        if (logger().isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void info(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> info(Marker marker) {
        if (logger().isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void warn(Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> warn() {
        if (logger().isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void warn(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> warn(Marker marker) {
        if (logger().isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void error(Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> error() {
        if (logger().isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void error(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> error(Marker marker) {
        if (logger().isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

}