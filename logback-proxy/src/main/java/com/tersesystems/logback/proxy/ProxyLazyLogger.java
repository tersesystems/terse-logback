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
package com.tersesystems.logback.proxy;

import org.slf4j.Logger;
import org.slf4j.Marker;
import java.util.function.*;
import java.util.Optional;


/**
 *
 */
public interface ProxyLazyLogger extends LazyLogger {

    Logger logger();
    
    default boolean isTraceEnabled() {
        return logger().isTraceEnabled();
    }

    default boolean isTraceEnabled(Marker marker) {
        return logger().isTraceEnabled(marker);
    }

    default void trace(Consumer<LoggerStatement> lc) {
        if (isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> trace() {
        if (isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void trace(Marker marker, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> trace(Marker marker) {
        if (isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default boolean isDebugEnabled() {
        return logger().isDebugEnabled();
    }

    default boolean isDebugEnabled(Marker marker) {
        return logger().isDebugEnabled(marker);
    }

    default void debug(Consumer<LoggerStatement> lc) {
        if (isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> debug() {
        if (isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void debug(Marker marker, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> debug(Marker marker) {
        if (isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default boolean isInfoEnabled() {
        return logger().isInfoEnabled();
    }

    default boolean isInfoEnabled(Marker marker) {
        return logger().isInfoEnabled(marker);
    }

    default void info(Consumer<LoggerStatement> lc) {
        if (isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> info() {
        if (isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void info(Marker marker, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> info(Marker marker) {
        if (isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default boolean isWarnEnabled() {
        return logger().isWarnEnabled();
    }

    default boolean isWarnEnabled(Marker marker) {
        return logger().isWarnEnabled(marker);
    }

    default void warn(Consumer<LoggerStatement> lc) {
        if (isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> warn() {
        if (isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void warn(Marker marker, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> warn(Marker marker) {
        if (isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default boolean isErrorEnabled() {
        return logger().isErrorEnabled();
    }

    default boolean isErrorEnabled(Marker marker) {
        return logger().isErrorEnabled(marker);
    }

    default void error(Consumer<LoggerStatement> lc) {
        if (isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> error() {
        if (isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    default void error(Marker marker, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            lc.accept(stmt);
        }
    }

    default Optional<LoggerStatement> error(Marker marker) {
        if (isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger());
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

}