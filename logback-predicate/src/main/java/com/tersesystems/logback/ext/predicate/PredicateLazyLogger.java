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

import com.tersesystems.logback.ext.LazyLogger;
import com.tersesystems.logback.ext.LoggerStatement;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface PredicateLazyLogger extends LazyLogger {

    Logger logger();

    Predicate<Level> predicate();

    @Override
    default void trace(Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled() && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> trace() {
        return logger().isTraceEnabled() && predicate().test(Level.TRACE)
                ? Optional.of(new LoggerStatement.Trace(logger()))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> trace(Marker marker) {
        return logger().isTraceEnabled(marker) && predicate().test(Level.TRACE)
                ? Optional.of(new LoggerStatement.Trace(logger()))
                : Optional.empty();
    }

    @Override
    default void trace(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled(marker) && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(logger()));
        }
    }

    @Override
    default void debug(Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled() && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> debug() {
        return logger().isDebugEnabled() && predicate().test(Level.DEBUG)
                ? Optional.of(new LoggerStatement.Debug(logger()))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> debug(Marker marker) {
        return logger().isDebugEnabled(marker) && predicate().test(Level.DEBUG)
                ? Optional.of(new LoggerStatement.Debug(logger()))
                : Optional.empty();
    }

    @Override
    default void debug(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled(marker) && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(logger()));
        }
    }

    @Override
    default void info(Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled() && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> info() {
        return logger().isInfoEnabled() && predicate().test(Level.INFO)
                ? Optional.of(new LoggerStatement.Info(logger()))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> info(Marker marker) {
        return logger().isInfoEnabled(marker) && predicate().test(Level.INFO)
                ? Optional.of(new LoggerStatement.Info(logger()))
                : Optional.empty();
    }

    @Override
    default void info(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled(marker) && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(logger()));
        }
    }


    @Override
    default void warn(Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled() && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> warn() {
        return logger().isWarnEnabled() && predicate().test(Level.WARN)
                ? Optional.of(new LoggerStatement.Warn(logger()))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> warn(Marker marker) {
        return logger().isWarnEnabled(marker) && predicate().test(Level.WARN)
                ? Optional.of(new LoggerStatement.Warn(logger()))
                : Optional.empty();
    }

    @Override
    default void warn(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled(marker) && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(logger()));
        }
    }

    @Override
    default void error(Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled() && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> error() {
        return logger().isErrorEnabled() && predicate().test(Level.ERROR)
                ? Optional.of(new LoggerStatement.Error(logger()))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> error(Marker marker) {
        return logger().isErrorEnabled(marker) && predicate().test(Level.ERROR)
                ? Optional.of(new LoggerStatement.Error(logger()))
                : Optional.empty();
    }

    @Override
    default void error(Marker marker, Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled(marker) && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(logger()));
        }
    }
}
