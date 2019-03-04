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
import org.slf4j.event.Level;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.Optional;


/**
 *
 */
public interface ProxyConditionalLogger extends ProxyLogger, ConditionalLogger, Logger, LazyLogger {

    Predicate<Level> predicate();

    @Override
    default void trace(Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    default Optional<LoggerStatement> trace() {
        return isTraceEnabled()
                ? Optional.of(new LoggerStatement.Trace(this))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> trace(Marker marker) {
        return isTraceEnabled(marker)
                ? Optional.of(new LoggerStatement.Trace(this))
                : Optional.empty();
    }

    @Override
    default void trace(Marker marker, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled(marker) && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    default void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && condition.get() && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    default void ifTrace(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled(marker) && condition.get() && predicate().test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    default Optional<LoggerStatement> ifTrace(Supplier<Boolean> condition) {
        if (isTraceEnabled() && condition.get() && predicate().test(Level.TRACE)) {
            return Optional.of(new LoggerStatement.Trace(this));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifTrace(Marker marker, Supplier<Boolean> condition) {
        if (isTraceEnabled(marker) && condition.get() && predicate().test(Level.TRACE)) {
            return Optional.of(new LoggerStatement.Trace(this));
        }
        return Optional.empty();
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
    default void debug(Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    default Optional<LoggerStatement> debug() {
        return isDebugEnabled()
                ? Optional.of(new LoggerStatement.Debug(this))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> debug(Marker marker) {
        return isDebugEnabled(marker)
                ? Optional.of(new LoggerStatement.Debug(this))
                : Optional.empty();
    }

    @Override
    default void debug(Marker marker, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled(marker) && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    default void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && condition.get() && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    default void ifDebug(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled(marker) && condition.get() && predicate().test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    default Optional<LoggerStatement> ifDebug(Supplier<Boolean> condition) {
        if (isDebugEnabled() && condition.get() && predicate().test(Level.DEBUG)) {
            return Optional.of(new LoggerStatement.Debug(this));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifDebug(Marker marker, Supplier<Boolean> condition) {
        if (isDebugEnabled(marker) && condition.get() && predicate().test(Level.DEBUG)) {
            return Optional.of(new LoggerStatement.Debug(this));
        }
        return Optional.empty();
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
    default void info(Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    default Optional<LoggerStatement> info() {
        return isInfoEnabled()
                ? Optional.of(new LoggerStatement.Info(this))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> info(Marker marker) {
        return isInfoEnabled(marker)
                ? Optional.of(new LoggerStatement.Info(this))
                : Optional.empty();
    }

    @Override
    default void info(Marker marker, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled(marker) && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    default void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && condition.get() && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    default void ifInfo(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled(marker) && condition.get() && predicate().test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    default Optional<LoggerStatement> ifInfo(Supplier<Boolean> condition) {
        if (isInfoEnabled() && condition.get() && predicate().test(Level.INFO)) {
            return Optional.of(new LoggerStatement.Info(this));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifInfo(Marker marker, Supplier<Boolean> condition) {
        if (isInfoEnabled(marker) && condition.get() && predicate().test(Level.INFO)) {
            return Optional.of(new LoggerStatement.Info(this));
        }
        return Optional.empty();
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
    default void warn(Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    default Optional<LoggerStatement> warn() {
        return isWarnEnabled()
                ? Optional.of(new LoggerStatement.Warn(this))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> warn(Marker marker) {
        return isWarnEnabled(marker)
                ? Optional.of(new LoggerStatement.Warn(this))
                : Optional.empty();
    }

    @Override
    default void warn(Marker marker, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled(marker) && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    default void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && condition.get() && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    default void ifWarn(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled(marker) && condition.get() && predicate().test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    default Optional<LoggerStatement> ifWarn(Supplier<Boolean> condition) {
        if (isWarnEnabled() && condition.get() && predicate().test(Level.WARN)) {
            return Optional.of(new LoggerStatement.Warn(this));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifWarn(Marker marker, Supplier<Boolean> condition) {
        if (isWarnEnabled(marker) && condition.get() && predicate().test(Level.WARN)) {
            return Optional.of(new LoggerStatement.Warn(this));
        }
        return Optional.empty();
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
    default void error(Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    default Optional<LoggerStatement> error() {
        return isErrorEnabled()
                ? Optional.of(new LoggerStatement.Error(this))
                : Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> error(Marker marker) {
        return isErrorEnabled(marker)
                ? Optional.of(new LoggerStatement.Error(this))
                : Optional.empty();
    }

    @Override
    default void error(Marker marker, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled(marker) && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    default void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && condition.get() && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    default void ifError(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled(marker) && condition.get() && predicate().test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    default Optional<LoggerStatement> ifError(Supplier<Boolean> condition) {
        if (isErrorEnabled() && condition.get() && predicate().test(Level.ERROR)) {
            return Optional.of(new LoggerStatement.Error(this));
        }
        return Optional.empty();
    }

    @Override
    default Optional<LoggerStatement> ifError(Marker marker, Supplier<Boolean> condition) {
        if (isErrorEnabled(marker) && condition.get() && predicate().test(Level.ERROR)) {
            return Optional.of(new LoggerStatement.Error(this));
        }
        return Optional.empty();
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