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
package com.tersesystems.logback.ext;

import org.slf4j.Logger;
import org.slf4j.Marker;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Optional;

/**
 *  A conditional logger with default interfaces.
 */
public interface ProxyConditionalLogger extends ConditionalLogger {

    Logger logger();

    @Override
    default void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled() && condition.get() ) {
            lc.accept(new LoggerStatement.Trace(logger()));
        }
    }

    @Override
    default void ifTrace(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isTraceEnabled(marker) && condition.get() ) {
            lc.accept(new LoggerStatement.Trace(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> ifTrace(Supplier<Boolean> condition) {
        if (logger().isTraceEnabled() && condition.get() ) {
            return Optional.of(new LoggerStatement.Trace(logger()));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifTrace(Marker marker, Supplier<Boolean> condition) {
        if (logger().isTraceEnabled(marker) && condition.get() ) {
            return Optional.of(new LoggerStatement.Trace(logger()));
        }
        return Optional.empty();
    }

    @Override
    default void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled() && condition.get() ) {
            lc.accept(new LoggerStatement.Debug(logger()));
        }
    }

    @Override
    default void ifDebug(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isDebugEnabled(marker) && condition.get() ) {
            lc.accept(new LoggerStatement.Debug(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> ifDebug(Supplier<Boolean> condition) {
        if (logger().isDebugEnabled() && condition.get() ) {
            return Optional.of(new LoggerStatement.Debug(logger()));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifDebug(Marker marker, Supplier<Boolean> condition) {
        if (logger().isDebugEnabled(marker) && condition.get() ) {
            return Optional.of(new LoggerStatement.Debug(logger()));
        }
        return Optional.empty();
    }

    @Override
    default void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled() && condition.get() ) {
            lc.accept(new LoggerStatement.Info(logger()));
        }
    }

    @Override
    default void ifInfo(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isInfoEnabled(marker) && condition.get() ) {
            lc.accept(new LoggerStatement.Info(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> ifInfo(Supplier<Boolean> condition) {
        if (logger().isInfoEnabled() && condition.get() ) {
            return Optional.of(new LoggerStatement.Info(logger()));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifInfo(Marker marker, Supplier<Boolean> condition) {
        if (logger().isInfoEnabled(marker) && condition.get() ) {
            return Optional.of(new LoggerStatement.Info(logger()));
        }
        return Optional.empty();
    }

    @Override
    default void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled() && condition.get() ) {
            lc.accept(new LoggerStatement.Warn(logger()));
        }
    }

    @Override
    default void ifWarn(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isWarnEnabled(marker) && condition.get() ) {
            lc.accept(new LoggerStatement.Warn(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> ifWarn(Supplier<Boolean> condition) {
        if (logger().isWarnEnabled() && condition.get() ) {
            return Optional.of(new LoggerStatement.Warn(logger()));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifWarn(Marker marker, Supplier<Boolean> condition) {
        if (logger().isWarnEnabled(marker) && condition.get() ) {
            return Optional.of(new LoggerStatement.Warn(logger()));
        }
        return Optional.empty();
    }

    @Override
    default void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Error(logger()));
        }
    }

    @Override
    default void ifError(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (logger().isErrorEnabled(marker) && condition.get()) {
            lc.accept(new LoggerStatement.Error(logger()));
        }
    }

    @Override
    default Optional<LoggerStatement> ifError(Supplier<Boolean> condition) {
        if (logger().isErrorEnabled() && condition.get()) {
            return Optional.of(new LoggerStatement.Error(logger()));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifError(Marker marker, Supplier<Boolean> condition) {
        if (logger().isErrorEnabled(marker) && condition.get()) {
            return Optional.of(new LoggerStatement.Error(logger()));
        }
        return Optional.empty();
    }

}