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
    private final MarkerContext markerContext;

    public ProxyContextLogger(MarkerContext markerContext, Logger logger) {
        if (logger instanceof ProxyContextLogger) {
            this.markerContext = markerContext.and(((ProxyContextLogger) logger).markerContext);
        } else {
            this.markerContext = markerContext;
        }
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled(markerContext.asMarker());
    }

    @Override
    public void trace(String msg) {
        logger.trace(markerContext.asMarker(), msg);
    }

    @Override
    public void trace(String format, Object arg) {
        logger.trace(markerContext.asMarker(), format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logger.trace(markerContext.asMarker(), format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger.trace(markerContext.asMarker(), format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger.trace(markerContext.asMarker(), msg, t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(markerContext.withMarker(marker).asMarker());
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(markerContext.withMarker(marker).asMarker(), msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(markerContext.withMarker(marker).asMarker(), format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(markerContext.withMarker(marker).asMarker(), format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(markerContext.withMarker(marker).asMarker(), format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(markerContext.withMarker(marker).asMarker(), msg, t);
    }


    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled(markerContext.asMarker());
    }

    @Override
    public void debug(String msg) {
        logger.debug(markerContext.asMarker(), msg);
    }

    @Override
    public void debug(String format, Object arg) {
        logger.debug(markerContext.asMarker(), format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logger.debug(markerContext.asMarker(), format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger.debug(markerContext.asMarker(), format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger.debug(markerContext.asMarker(), msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(markerContext.withMarker(marker).asMarker());
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(markerContext.withMarker(marker).asMarker(), msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(markerContext.withMarker(marker).asMarker(), format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(markerContext.withMarker(marker).asMarker(), format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        logger.debug(markerContext.withMarker(marker).asMarker(), format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(markerContext.withMarker(marker).asMarker(), msg, t);
    }


    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled(markerContext.asMarker());
    }

    @Override
    public void info(String msg) {
        logger.info(markerContext.asMarker(), msg);
    }

    @Override
    public void info(String format, Object arg) {
        logger.info(markerContext.asMarker(), format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logger.info(markerContext.asMarker(), format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger.info(markerContext.asMarker(), format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger.info(markerContext.asMarker(), msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(markerContext.withMarker(marker).asMarker());
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(markerContext.withMarker(marker).asMarker(), msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(markerContext.withMarker(marker).asMarker(), format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(markerContext.withMarker(marker).asMarker(), format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        logger.info(markerContext.withMarker(marker).asMarker(), format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(markerContext.withMarker(marker).asMarker(), msg, t);
    }


    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled(markerContext.asMarker());
    }

    @Override
    public void warn(String msg) {
        logger.warn(markerContext.asMarker(), msg);
    }

    @Override
    public void warn(String format, Object arg) {
        logger.warn(markerContext.asMarker(), format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logger.warn(markerContext.asMarker(), format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger.warn(markerContext.asMarker(), format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger.warn(markerContext.asMarker(), msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(markerContext.withMarker(marker).asMarker());
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(markerContext.withMarker(marker).asMarker(), msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(markerContext.withMarker(marker).asMarker(), format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(markerContext.withMarker(marker).asMarker(), format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        logger.warn(markerContext.withMarker(marker).asMarker(), format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(markerContext.withMarker(marker).asMarker(), msg, t);
    }


    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled(markerContext.asMarker());
    }

    @Override
    public void error(String msg) {
        logger.error(markerContext.asMarker(), msg);
    }

    @Override
    public void error(String format, Object arg) {
        logger.error(markerContext.asMarker(), format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logger.error(markerContext.asMarker(), format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger.error(markerContext.asMarker(), format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(markerContext.asMarker(), msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(markerContext.withMarker(marker).asMarker());
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(markerContext.withMarker(marker).asMarker(), msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(markerContext.withMarker(marker).asMarker(), format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(markerContext.withMarker(marker).asMarker(), format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        logger.error(markerContext.withMarker(marker).asMarker(), format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(markerContext.withMarker(marker).asMarker(), msg, t);
    }


}
