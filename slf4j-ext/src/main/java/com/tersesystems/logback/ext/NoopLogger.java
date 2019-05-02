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

import org.slf4j.*;


/**
 * A logger interface with default methods that do nothing.
 */
public interface NoopLogger extends Logger {

    @Override
    default boolean isTraceEnabled() {
        return false;
    }

    @Override
    default void trace(String msg) {
    }

    @Override
    default void trace(String format, Object arg) {
    }

    @Override
    default void trace(String format, Object arg1, Object arg2) {
    }

    @Override
    default void trace(String format, Object... arguments) {
    }

    @Override
    default void trace(String msg, Throwable t) {
    }

    @Override
    default boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    default void trace(Marker marker, String msg) {
    }

    @Override
    default void trace(Marker marker, String format, Object arg) {
    }

    @Override
    default void trace(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    default void trace(Marker marker, String format, Object... argArray) {
    }

    @Override
    default void trace(Marker marker, String msg, Throwable t) {
    }

    @Override
    default boolean isDebugEnabled() {
        return false;
    }

    @Override
    default void debug(String msg) {
    }

    @Override
    default void debug(String format, Object arg) {
    }

    @Override
    default void debug(String format, Object arg1, Object arg2) {
    }

    @Override
    default void debug(String format, Object... arguments) {
    }

    @Override
    default void debug(String msg, Throwable t) {
    }

    @Override
    default boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    default void debug(Marker marker, String msg) {
    }

    @Override
    default void debug(Marker marker, String format, Object arg) {
    }

    @Override
    default void debug(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    default void debug(Marker marker, String format, Object... argArray) {
    }

    @Override
    default void debug(Marker marker, String msg, Throwable t) {
    }

    @Override
    default boolean isInfoEnabled() {
        return false;
    }

    @Override
    default void info(String msg) {
    }

    @Override
    default void info(String format, Object arg) {
    }

    @Override
    default void info(String format, Object arg1, Object arg2) {
    }

    @Override
    default void info(String format, Object... arguments) {
    }

    @Override
    default void info(String msg, Throwable t) {
    }

    @Override
    default boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    default void info(Marker marker, String msg) {
    }

    @Override
    default void info(Marker marker, String format, Object arg) {
    }

    @Override
    default void info(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    default void info(Marker marker, String format, Object... argArray) {
    }

    @Override
    default void info(Marker marker, String msg, Throwable t) {
    }

    @Override
    default boolean isWarnEnabled() {
        return false;
    }

    @Override
    default void warn(String msg) {
    }

    @Override
    default void warn(String format, Object arg) {
    }

    @Override
    default void warn(String format, Object arg1, Object arg2) {
    }

    @Override
    default void warn(String format, Object... arguments) {
    }

    @Override
    default void warn(String msg, Throwable t) {
    }

    @Override
    default boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    default void warn(Marker marker, String msg) {
    }

    @Override
    default void warn(Marker marker, String format, Object arg) {
    }

    @Override
    default void warn(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    default void warn(Marker marker, String format, Object... argArray) {
    }

    @Override
    default void warn(Marker marker, String msg, Throwable t) {
    }

    @Override
    default boolean isErrorEnabled() {
        return false;
    }

    @Override
    default void error(String msg) {
    }

    @Override
    default void error(String format, Object arg) {
    }

    @Override
    default void error(String format, Object arg1, Object arg2) {
    }

    @Override
    default void error(String format, Object... arguments) {
    }

    @Override
    default void error(String msg, Throwable t) {
    }

    @Override
    default boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    default void error(Marker marker, String msg) {
    }

    @Override
    default void error(Marker marker, String format, Object arg) {
    }

    @Override
    default void error(Marker marker, String format, Object arg1, Object arg2) {
    }

    @Override
    default void error(Marker marker, String format, Object... argArray) {
    }

    @Override
    default void error(Marker marker, String msg, Throwable t) {
    }

}
