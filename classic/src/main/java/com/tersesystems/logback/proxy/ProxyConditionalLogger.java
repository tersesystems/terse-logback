package com.tersesystems.logback.proxy;

import org.slf4j.Logger;
import org.slf4j.Marker;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ProxyConditionalLogger extends ProxyLogger implements ConditionalLogger, Logger {

    private final Supplier<Boolean> condition;

    public ProxyConditionalLogger(Logger logger) {
        super(logger);
        if (logger instanceof ConditionalLogger) {
          this.condition = ((ConditionalLogger) logger).getCondition();
        } else {
          this.condition = () -> true;
        }
    }

    public ProxyConditionalLogger(Logger logger, Supplier<Boolean> condition) {
        super(logger);
        if (logger instanceof ConditionalLogger) {
          this.condition = () -> ((ConditionalLogger) logger).getCondition().get() && condition.get();
        } else {
          this.condition = condition;
        }
    }

    public Supplier<Boolean> getCondition() {
        return condition;
    }

    

    @Override
    public void ifTrace(Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }

    @Override
    public void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isTraceEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Trace(this));
        }
    }


    @Override
    public boolean isTraceEnabled() {
        return super.isTraceEnabled() && condition.get();
    }

    @Override
    public void trace(String msg) {
        if (condition.get()) {
          super.trace(msg);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (condition.get()) {
          super.trace(format, arg);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (condition.get()) {
          super.trace(format, arg1, arg2);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (condition.get()) {
          super.trace(format, arguments);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (condition.get()) {
            super.trace(msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return super.isTraceEnabled(marker) && (condition.get()) ;
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (condition.get()) {
            super.trace(marker, msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (condition.get()) {
            super.trace(marker, format, arg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (condition.get()) {
            super.trace(marker, format, arg1, arg2);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (condition.get()) {
            super.trace(marker, format, argArray);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (condition.get()) {
            super.trace(marker, msg, t);
        }
    }
    

    @Override
    public void ifDebug(Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }

    @Override
    public void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isDebugEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Debug(this));
        }
    }


    @Override
    public boolean isDebugEnabled() {
        return super.isDebugEnabled() && condition.get();
    }

    @Override
    public void debug(String msg) {
        if (condition.get()) {
          super.debug(msg);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (condition.get()) {
          super.debug(format, arg);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (condition.get()) {
          super.debug(format, arg1, arg2);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (condition.get()) {
          super.debug(format, arguments);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (condition.get()) {
            super.debug(msg, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return super.isDebugEnabled(marker) && (condition.get()) ;
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (condition.get()) {
            super.debug(marker, msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (condition.get()) {
            super.debug(marker, format, arg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (condition.get()) {
            super.debug(marker, format, arg1, arg2);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        if (condition.get()) {
            super.debug(marker, format, argArray);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (condition.get()) {
            super.debug(marker, msg, t);
        }
    }
    

    @Override
    public void ifInfo(Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }

    @Override
    public void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isInfoEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Info(this));
        }
    }


    @Override
    public boolean isInfoEnabled() {
        return super.isInfoEnabled() && condition.get();
    }

    @Override
    public void info(String msg) {
        if (condition.get()) {
          super.info(msg);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (condition.get()) {
          super.info(format, arg);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (condition.get()) {
          super.info(format, arg1, arg2);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (condition.get()) {
          super.info(format, arguments);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (condition.get()) {
            super.info(msg, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return super.isInfoEnabled(marker) && (condition.get()) ;
    }

    @Override
    public void info(Marker marker, String msg) {
        if (condition.get()) {
            super.info(marker, msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (condition.get()) {
            super.info(marker, format, arg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (condition.get()) {
            super.info(marker, format, arg1, arg2);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        if (condition.get()) {
            super.info(marker, format, argArray);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (condition.get()) {
            super.info(marker, msg, t);
        }
    }
    

    @Override
    public void ifWarn(Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }

    @Override
    public void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isWarnEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Warn(this));
        }
    }


    @Override
    public boolean isWarnEnabled() {
        return super.isWarnEnabled() && condition.get();
    }

    @Override
    public void warn(String msg) {
        if (condition.get()) {
          super.warn(msg);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (condition.get()) {
          super.warn(format, arg);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (condition.get()) {
          super.warn(format, arg1, arg2);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (condition.get()) {
          super.warn(format, arguments);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (condition.get()) {
            super.warn(msg, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return super.isWarnEnabled(marker) && (condition.get()) ;
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (condition.get()) {
            super.warn(marker, msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (condition.get()) {
            super.warn(marker, format, arg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (condition.get()) {
            super.warn(marker, format, arg1, arg2);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        if (condition.get()) {
            super.warn(marker, format, argArray);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (condition.get()) {
            super.warn(marker, msg, t);
        }
    }
    

    @Override
    public void ifError(Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }

    @Override
    public void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc) {
        if (isErrorEnabled() && condition.get()) {
            lc.accept(new LoggerStatement.Error(this));
        }
    }


    @Override
    public boolean isErrorEnabled() {
        return super.isErrorEnabled() && condition.get();
    }

    @Override
    public void error(String msg) {
        if (condition.get()) {
          super.error(msg);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (condition.get()) {
          super.error(format, arg);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (condition.get()) {
          super.error(format, arg1, arg2);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (condition.get()) {
          super.error(format, arguments);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (condition.get()) {
            super.error(msg, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return super.isErrorEnabled(marker) && (condition.get()) ;
    }

    @Override
    public void error(Marker marker, String msg) {
        if (condition.get()) {
            super.error(marker, msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (condition.get()) {
            super.error(marker, format, arg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (condition.get()) {
            super.error(marker, format, arg1, arg2);
        }
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        if (condition.get()) {
            super.error(marker, format, argArray);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (condition.get()) {
            super.error(marker, msg, t);
        }
    }
    
}