package com.tersesystems.logback.proxy;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Predicate;


public class ProxyConditionalLogger extends ProxyLogger implements ConditionalLogger, Logger {

    private final Predicate<Level> predicate;

    public ProxyConditionalLogger(Logger logger) {
        super(logger);
        if (logger instanceof ConditionalLogger) {
            this.predicate = ((ConditionalLogger) logger).getPredicate();
        } else {
            this.predicate = level -> true;
        }
    }

    public ProxyConditionalLogger(Logger logger, Predicate<Level> predicate) {
        super(logger);
        if (logger instanceof ConditionalLogger) {
            this.predicate = level -> ((ConditionalLogger) logger).getPredicate().test(level) && predicate.test(level);
        } else {
            this.predicate = predicate;
        }
    }

    public Predicate<Level> getPredicate() {
        return predicate;
    }

    @Override
    public void ifTrace(Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && predicate.test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    public void ifTrace(Supplier<Boolean> supplier, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && supplier.get() && predicate.test(Level.TRACE)) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return super.isTraceEnabled() && predicate.test(Level.TRACE);
    }

    @Override
    public void trace(String msg) {
        if (predicate.test(Level.TRACE)) {
            super.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (predicate.test(Level.TRACE)) {
            super.trace(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (predicate.test(Level.TRACE)) {
            super.trace(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (predicate.test(Level.TRACE)) {
            super.trace(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (predicate.test(Level.TRACE)) {
            super.trace(msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return super.isTraceEnabled(marker) && predicate.test(Level.TRACE);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (predicate.test(Level.TRACE)) {
            super.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (predicate.test(Level.TRACE)) {
            super.trace(marker, format, arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate.test(Level.TRACE)) {
            super.trace(marker, format, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (predicate.test(Level.TRACE)) {
            super.trace(marker, format, argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (predicate.test(Level.TRACE)) {
            super.trace(marker, msg, t);
        }
    }


    @Override
    public void ifDebug(Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && predicate.test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    public void ifDebug(Supplier<Boolean> supplier, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && supplier.get() && predicate.test(Level.DEBUG)) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }


    @Override
    public boolean isDebugEnabled() {
        return super.isDebugEnabled() && predicate.test(Level.DEBUG);
    }

    @Override
    public void debug(String msg) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return super.isDebugEnabled(marker) && predicate.test(Level.DEBUG);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(marker, format, arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(marker, format, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(marker, format, argArray);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (predicate.test(Level.DEBUG)) {
            super.debug(marker, msg, t);
        }
    }


    @Override
    public void ifInfo(Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && predicate.test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    public void ifInfo(Supplier<Boolean> supplier, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && supplier.get() && predicate.test(Level.INFO)) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }


    @Override
    public boolean isInfoEnabled() {
        return super.isInfoEnabled() && predicate.test(Level.INFO);
    }

    @Override
    public void info(String msg) {
        if (predicate.test(Level.INFO)) {
            super.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (predicate.test(Level.INFO)) {
            super.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (predicate.test(Level.INFO)) {
            super.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (predicate.test(Level.INFO)) {
            super.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (predicate.test(Level.INFO)) {
            super.info(msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return super.isInfoEnabled(marker) && predicate.test(Level.INFO);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (predicate.test(Level.INFO)) {
            super.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (predicate.test(Level.INFO)) {
            super.info(marker, format, arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate.test(Level.INFO)) {
            super.info(marker, format, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        if (predicate.test(Level.INFO)) {
            super.info(marker, format, argArray);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (predicate.test(Level.INFO)) {
            super.info(marker, msg, t);
        }
    }


    @Override
    public void ifWarn(Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && predicate.test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    public void ifWarn(Supplier<Boolean> supplier, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && supplier.get() && predicate.test(Level.WARN)) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }


    @Override
    public boolean isWarnEnabled() {
        return super.isWarnEnabled() && predicate.test(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        if (predicate.test(Level.WARN)) {
            super.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (predicate.test(Level.WARN)) {
            super.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (predicate.test(Level.WARN)) {
            super.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (predicate.test(Level.WARN)) {
            super.warn(format, arguments);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (predicate.test(Level.WARN)) {
            super.warn(msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return super.isWarnEnabled(marker) && predicate.test(Level.WARN);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (predicate.test(Level.WARN)) {
            super.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (predicate.test(Level.WARN)) {
            super.warn(marker, format, arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate.test(Level.WARN)) {
            super.warn(marker, format, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        if (predicate.test(Level.WARN)) {
            super.warn(marker, format, argArray);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (predicate.test(Level.WARN)) {
            super.warn(marker, msg, t);
        }
    }


    @Override
    public void ifError(Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && predicate.test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    public void ifError(Supplier<Boolean> supplier, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && supplier.get() && predicate.test(Level.ERROR)) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }


    @Override
    public boolean isErrorEnabled() {
        return super.isErrorEnabled() && predicate.test(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        if (predicate.test(Level.ERROR)) {
            super.error(msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (predicate.test(Level.ERROR)) {
            super.error(format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (predicate.test(Level.ERROR)) {
            super.error(format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (predicate.test(Level.ERROR)) {
            super.error(format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (predicate.test(Level.ERROR)) {
            super.error(msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return super.isErrorEnabled(marker) && predicate.test(Level.ERROR);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (predicate.test(Level.ERROR)) {
            super.error(marker, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (predicate.test(Level.ERROR)) {
            super.error(marker, format, arg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (predicate.test(Level.ERROR)) {
            super.error(marker, format, arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        if (predicate.test(Level.ERROR)) {
            super.error(marker, format, argArray);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (predicate.test(Level.ERROR)) {
            super.error(marker, msg, t);
        }
    }

}