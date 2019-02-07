package com.tersesystems.logback.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Proxy logger that takes a marker as argument.
 */
public class ProxyContextLogger<T extends Marker> implements Logger, ContextAware<T> {

    private final Logger logger;
    private final Context<T> context;

    public ProxyContextLogger(Context<T> context, Logger logger) {
        if (logger instanceof ContextAware) {
            this.context = context.and(((ContextAware) logger).getContext());
        } else {
            this.context = context;
        }
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public Context<T> getContext() {
        return this.context;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled(context.asMarker());
    }

    @Override
    public void trace(String msg) {
        logger.trace(context.asMarker(), msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(context.asMarker(), format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(context.asMarker(), format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(context.asMarker(), format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(context.asMarker(), msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        return logger.isTraceEnabled(contextMarker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.trace(contextMarker, msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.trace(contextMarker, format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.trace(contextMarker, format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.trace(contextMarker, format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.trace(contextMarker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled(context.asMarker());
    }

    @Override
    public void debug(String msg) {
        logger.debug(context.asMarker(), msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(context.asMarker(), format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(context.asMarker(), format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(context.asMarker(), format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(context.asMarker(), msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        return logger.isDebugEnabled(contextMarker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.debug(contextMarker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.debug(contextMarker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.debug(contextMarker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.debug(contextMarker, format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.debug(contextMarker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled(context.asMarker());
    }

    @Override
    public void info(String msg) {
        logger.info(context.asMarker(), msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(context.asMarker(), format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(context.asMarker(), format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(context.asMarker(), format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(context.asMarker(), msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        return logger.isInfoEnabled(contextMarker);
    }

    @Override
    public void info(Marker marker, String msg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.info(contextMarker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.info(contextMarker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.info(contextMarker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.info(contextMarker, format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.info(contextMarker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled(context.asMarker());
    }

    @Override
    public void warn(String msg) {
        logger.warn(context.asMarker(), msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(context.asMarker(), format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(context.asMarker(), format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(context.asMarker(), format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(context.asMarker(), msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        return logger.isWarnEnabled(contextMarker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.warn(contextMarker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.warn(contextMarker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.warn(contextMarker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.warn(contextMarker, format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.warn(contextMarker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled(context.asMarker());
    }

    @Override
    public void error(String msg) {
        logger.error(context.asMarker(), msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(context.asMarker(), format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(context.asMarker(), format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(context.asMarker(), format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(context.asMarker(), msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        return logger.isErrorEnabled(contextMarker);
    }

    @Override
    public void error(Marker marker, String msg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.error(contextMarker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.error(contextMarker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.error(contextMarker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.error(contextMarker, format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        Marker contextMarker = context.asMarker();
        contextMarker.add(marker);
        logger.error(contextMarker, msg, t);
    }

}