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
package com.tersesystems.logback.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Proxy logger that takes a marker as argument.
 */
public abstract class AbstractContextLogger<
        MarkerT extends Marker,
        ContextT extends Context<MarkerT, ContextT>,
        LoggerT extends Logger,
        SelfT
        > implements LoggerWithContext<MarkerT, ContextT, SelfT> {

    protected final LoggerT logger;
    protected final ContextT context;

    public AbstractContextLogger(ContextT context, LoggerT logger) {
        this.context = context;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public ContextT getContext() {
        return this.context;
    }

    @Override
    public String toString() {
        return String.format("AbstractContextLogger(context = %s,logger = %s)", this.context, this.logger);
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
        return logger.isTraceEnabled(merge(marker));
    }

    @Override
    public void trace(Marker marker, String msg) {
        logger.trace(merge(marker), msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        logger.trace(merge(marker), format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        logger.trace(merge(marker), format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        logger.trace(merge(marker), format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        logger.trace(merge(marker), msg, t);
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
        return logger.isDebugEnabled(merge(marker));
    }

    @Override
    public void debug(Marker marker, String msg) {
        logger.debug(merge(marker), msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        logger.debug(merge(marker), format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        logger.debug(merge(marker), format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        logger.debug(merge(marker), format, argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        logger.debug(merge(marker), msg, t);
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
        return logger.isInfoEnabled(merge(marker));
    }

    @Override
    public void info(Marker marker, String msg) {
        logger.info(merge(marker), msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        logger.info(merge(marker), format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        logger.info(merge(marker), format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        logger.info(merge(marker), format, argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        logger.info(merge(marker), msg, t);
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
        return logger.isWarnEnabled(merge(marker));
    }

    @Override
    public void warn(Marker marker, String msg) {
        logger.warn(merge(marker), msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        logger.warn(merge(marker), format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        logger.warn(merge(marker), format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        logger.warn(merge(marker), format, argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        logger.warn(merge(marker), msg, t);
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
        return logger.isErrorEnabled(merge(marker));
    }

    @Override
    public void error(Marker marker, String msg) {
        logger.error(merge(marker), msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        logger.error(merge(marker), format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        logger.error(merge(marker), format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        logger.error(merge(marker), format, argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        logger.error(merge(marker), msg, t);
    }

    protected abstract Marker merge(Marker marker);

}