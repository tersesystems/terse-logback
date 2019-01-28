package com.tersesystems.logback.proxy;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Proxy logger that takes a marker as argument.
 * <p>
 * Done with https://github.com/jroper/play-source-generator automating most of it.
 */
public class ProxyContextLogger implements Logger {

    private final Logger logger;
    private final LogstashMarker context;

    public ProxyContextLogger(LogstashMarker context, Logger logger) {
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
        return logger.isTraceEnabled(context);
    }

    @Override
    public void trace(String msg) {
        logger.trace(context, msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(context, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(context, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(context, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(context, msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(context.and(marker));
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(context.and(marker), msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(context.and(marker), format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(context.and(marker), format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(context.and(marker), format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(context.and(marker), msg, t);
    }


    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled(context);
    }

    @Override
    public void debug(String msg) {
        logger.debug(context, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(context, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(context, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(context, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(context, msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(context.and(marker));
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(context.and(marker), msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(context.and(marker), format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(context.and(marker), format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        logger.debug(context.and(marker), format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(context.and(marker), msg, t);
    }


    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled(context);
    }

    @Override
    public void info(String msg) {
        logger.info(context, msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(context, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(context, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(context, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(context, msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(context.and(marker));
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(context.and(marker), msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(context.and(marker), format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(context.and(marker), format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        logger.info(context.and(marker), format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(context.and(marker), msg, t);
    }


    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled(context);
    }

    @Override
    public void warn(String msg) {
        logger.warn(context, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(context, format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(context, format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(context, format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(context, msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(context.and(marker));
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(context.and(marker), msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(context.and(marker), format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(context.and(marker), format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        logger.warn(context.and(marker), format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(context.and(marker), msg, t);
    }


    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled(context);
    }

    @Override
    public void error(String msg) {
        logger.error(context, msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(context, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(context, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(context, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(context, msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(context.and(marker));
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(context.and(marker), msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(context.and(marker), format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(context.and(marker), format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        logger.error(context.and(marker), format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(context.and(marker), msg, t);
    }


}
