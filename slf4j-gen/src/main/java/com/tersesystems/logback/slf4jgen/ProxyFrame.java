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

public interface ProxyFrame extends LoggerFrame {

    String delegate();

    default MethodSpec getNameMethodBody(MethodSpec.Builder builder) {
        return builder.addStatement("return $L.getName()", delegate()).build();
    }

    // public boolean isTraceEnabled();
    default MethodSpec isEnabledMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("return $L.$N()", delegate(), builder.build()).build();
    }

    // void info(String message);
    default MethodSpec stringMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(msg)", delegate(), builder.build()).build();
    }

    //     public void trace(String format, Object arg1);
    default MethodSpec stringArgMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(format, arg)", delegate(), builder.build()).build();
    }

    //     public void trace(String format, Object arg1, Object arg2);
    default MethodSpec stringArg1Arg2MethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(format, arg1, arg2)", delegate(), builder.build()).build();
    }

    //     public void trace(String format, Object... arguments);
    default MethodSpec stringArgArrayMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(format, arguments)", delegate(), builder.build()).build();
    }

    // public void trace(String msg, Throwable t);
    default MethodSpec stringThrowableMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(msg, t)", delegate(), builder.build()).build();
    }

    // public boolean isTraceEnabled(Marker marker);
    default MethodSpec isEnabledMarkerMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("return $L.$N(marker)", delegate(), builder.build()).build();
    }

    // public void trace(Marker marker, String msg);
    default MethodSpec markerStringMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(marker, msg)", delegate(), builder.build()).build();
    }

    // public void trace(Marker marker, String format, Object arg);
    default MethodSpec markerFormatArgMethod(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(marker, format, arg)", delegate(), builder.build()).build();
    }

    //     public void trace(Marker marker, String format, Object arg1, Object arg2);
    default MethodSpec markerFormatArg1Arg2MethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(marker, format, arg1, arg2)", delegate(), builder.build()).build();
    }

    //     public void trace(Marker marker, String format, Object... argArray);
    default MethodSpec markerFormatArgArrayMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(marker, format, argArray)", delegate(), builder.build()).build();
    }

    // public void trace(Marker marker, String msg, Throwable t);
    default MethodSpec markerMsgThrowableMethodBody(Level level, MethodSpec.Builder builder) {
        return builder.addStatement("$L.$N(marker, msg, t)", delegate(), builder.build()).build();
    }

}
