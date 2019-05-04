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
package com.tersesystems.logback.slf4jgen;

import com.squareup.javapoet.MethodSpec;
import org.slf4j.event.Level;

public interface LoggerFrame {

    // public String getName();
    MethodSpec getNameMethodBody(MethodSpec.Builder builder);

    // public boolean isTraceEnabled();
    MethodSpec isEnabledMethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(String message);
    MethodSpec stringMethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(String format, Object arg1);
    MethodSpec stringArgMethodBody(Level level, MethodSpec.Builder builder) ;

    // public void trace(String format, Object arg1, Object arg2);
    MethodSpec stringArg1Arg2MethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(String format, Object... arguments);
    MethodSpec stringArgArrayMethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(String msg, Throwable t);
     MethodSpec stringThrowableMethodBody(Level level, MethodSpec.Builder builder);

    // public boolean isTraceEnabled(Marker marker);
     MethodSpec isEnabledMarkerMethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(Marker marker, String msg);
    MethodSpec markerStringMethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(Marker marker, String format, Object arg);
    MethodSpec markerFormatArgMethod(Level level, MethodSpec.Builder builder);

    // public void trace(Marker marker, String format, Object arg1, Object arg2);
    MethodSpec markerFormatArg1Arg2MethodBody(Level level, MethodSpec.Builder builder);

    // public void trace(Marker marker, String format, Object... argArray);
    MethodSpec markerFormatArgArrayMethodBody(Level level, MethodSpec.Builder builder) ;

    // public void trace(Marker marker, String msg, Throwable t);
    MethodSpec markerMsgThrowableMethodBody(Level level, MethodSpec.Builder builder);

}
