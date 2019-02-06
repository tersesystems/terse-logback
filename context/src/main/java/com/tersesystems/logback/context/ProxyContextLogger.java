package com.tersesystems.logback.context;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Proxy logger that takes a marker as argument.
 */
public class ProxyContextLogger<T extends LogstashMarker> implements Logger {

    private final Logger logger;
    private final Context<T> context;

    public ProxyContextLogger(Context<T> context, Logger logger) {
        if (logger instanceof ProxyContextLogger) {
            this.context = context.and(((ProxyContextLogger) logger).context);
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
        return logger.isTraceEnabled(context.asMarker().and(marker));
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(context.asMarker().and(marker), msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(context.asMarker().and(marker), format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(context.asMarker().and(marker), format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(context.asMarker().and(marker), format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(context.asMarker().and(marker), msg, t);
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
        return logger.isDebugEnabled(context.asMarker().and(marker));
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(context.asMarker().and(marker), msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(context.asMarker().and(marker), format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(context.asMarker().and(marker), format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        logger.debug(context.asMarker().and(marker), format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(context.asMarker().and(marker), msg, t);
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
        return logger.isInfoEnabled(context.asMarker().and(marker));
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(context.asMarker().and(marker), msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(context.asMarker().and(marker), format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(context.asMarker().and(marker), format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        logger.info(context.asMarker().and(marker), format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(context.asMarker().and(marker), msg, t);
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
        return logger.isWarnEnabled(context.asMarker().and(marker));
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(context.asMarker().and(marker), msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(context.asMarker().and(marker), format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(context.asMarker().and(marker), format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        logger.warn(context.asMarker().and(marker), format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(context.asMarker().and(marker), msg, t);
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
        return logger.isErrorEnabled(context.asMarker().and(marker));
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(context.asMarker().and(marker), msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(context.asMarker().and(marker), format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(context.asMarker().and(marker), format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        logger.error(context.asMarker().and(marker), format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(context.asMarker().and(marker), msg, t);
    }


}
