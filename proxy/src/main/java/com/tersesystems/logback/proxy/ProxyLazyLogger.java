package com.tersesystems.logback.proxy;

import org.slf4j.Logger;
import org.slf4j.Marker;
import java.util.function.*;
import java.util.Optional;


public class ProxyLazyLogger implements LazyLogger {

    private final Logger logger;

    public ProxyLazyLogger(Logger logger) {
        this.logger = logger;
    }


    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    public void trace(Consumer<LoggerStatement> lc) {
        if (isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> trace() {
        if (isTraceEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public void trace(Marker marker, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> trace(Marker marker) {
        if (isTraceEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Trace(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    public void debug(Consumer<LoggerStatement> lc) {
        if (isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> debug() {
        if (isDebugEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public void debug(Marker marker, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> debug(Marker marker) {
        if (isDebugEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Debug(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    public void info(Consumer<LoggerStatement> lc) {
        if (isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> info() {
        if (isInfoEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Info(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public void info(Marker marker, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> info(Marker marker) {
        if (isInfoEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Info(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    public void warn(Consumer<LoggerStatement> lc) {
        if (isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> warn() {
        if (isWarnEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public void warn(Marker marker, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> warn(Marker marker) {
        if (isWarnEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Warn(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    public void error(Consumer<LoggerStatement> lc) {
        if (isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> error() {
        if (isErrorEnabled()) {
            LoggerStatement stmt = new LoggerStatement.Error(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

    public void error(Marker marker, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger);
            lc.accept(stmt);
        }
    }

    public Optional<LoggerStatement> error(Marker marker) {
        if (isErrorEnabled(marker)) {
            LoggerStatement stmt = new LoggerStatement.Error(logger);
            return Optional.of(stmt);
        } else {
            return Optional.empty();
        }
    }

}