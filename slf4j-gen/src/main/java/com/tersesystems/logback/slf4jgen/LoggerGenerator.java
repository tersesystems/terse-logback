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
import com.squareup.javapoet.TypeSpec;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class LoggerGenerator {

    private final TypeSpec.Builder typeBuilder;
    private final LoggerFrame frame;

    public LoggerGenerator(TypeSpec.Builder typeBuilder, LoggerFrame frame) {
        this.typeBuilder = typeBuilder;
        this.frame = frame;
    }

    public TypeSpec generate() {
        MethodSpec getNameMethod = frame.getNameMethodBody(MethodSpec.methodBuilder("getName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class));

        return typeBuilder
                .addMethod(getNameMethod)
                .addMethods(methods(frame, Level.TRACE))
                .addMethods(methods(frame, Level.DEBUG))
                .addMethods(methods(frame, Level.INFO))
                .addMethods(methods(frame, Level.WARN))
                .addMethods(methods(frame, Level.ERROR))
                .build();
    }

    protected String enabled(String level) {
        return "is" +  level.substring(0, 1).toUpperCase() + level.substring(1) + "Enabled";
    }

    protected Iterable<MethodSpec> methods(LoggerFrame gen, Level level) {
        String levelName = level.name().toLowerCase();

        MethodSpec isEnabledMethod = gen.isEnabledMethodBody(level, MethodSpec.methodBuilder(enabled(levelName))
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class));

        //     public void trace(String msg);
        MethodSpec stringMethod = gen.stringMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "msg"));

        //     public void trace(String format, Object arg);
        MethodSpec stringArgMethod = gen.stringArgMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg"));

        //     public void trace(String format, Object arg1, Object arg2);
        MethodSpec stringArg1Arg2Method = gen.stringArg1Arg2MethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg1")
                .addParameter(Object.class, "arg2"));

        //     public void trace(String format, Object... arguments);
        MethodSpec stringArgArrayMethod = gen.stringArgArrayMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "format")
                .addParameter(Object[].class, "arguments")
                .varargs(true));

        //public void trace(String msg, Throwable t);
        MethodSpec stringThrowableMethod = gen.stringThrowableMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class, "msg")
                .addParameter(Throwable.class, "t"));

        // public boolean isTraceEnabled(Marker marker);
        MethodSpec isEnabledMarkerMethod = gen.isEnabledMarkerMethodBody(level, MethodSpec.methodBuilder(enabled(levelName))
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Marker.class, "marker"));

        // public void trace(Marker marker, String msg);
        MethodSpec markerStringMethod = gen.markerStringMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "msg"));

        // public void trace(Marker marker, String format, Object arg);
        MethodSpec markerFormatArgMethod = gen.markerFormatArgMethod(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg"));

        //     public void trace(Marker marker, String format, Object arg1, Object arg2);
        MethodSpec markerFormatArg1Arg2Method = gen.markerFormatArg1Arg2MethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object.class, "arg1")
                .addParameter(Object.class, "arg2"));

        //     public void trace(Marker marker, String format, Object... argArray);
        MethodSpec markerFormatArgArrayMethod = gen.markerFormatArgArrayMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "format")
                .addParameter(Object[].class, "argArray")
                .varargs(true));

        // public void trace(Marker marker, String msg, Throwable t);
        MethodSpec markerMsgThrowableMethod = gen.markerMsgThrowableMethodBody(level, MethodSpec.methodBuilder(levelName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(Marker.class, "marker")
                .addParameter(String.class, "msg")
                .addParameter(Throwable.class, "t"));

        List<MethodSpec> methodList = new ArrayList<>();

        methodList.add(isEnabledMethod);
        methodList.add(stringMethod);
        methodList.add(stringArgMethod);
        methodList.add(stringArg1Arg2Method);
        methodList.add(stringThrowableMethod);
        methodList.add(stringArgArrayMethod);
        methodList.add(isEnabledMarkerMethod);
        methodList.add(markerStringMethod);
        methodList.add(markerFormatArgMethod);
        methodList.add(markerFormatArg1Arg2Method);
        methodList.add(markerFormatArgArrayMethod);
        methodList.add(markerMsgThrowableMethod);
        return methodList;
    }
}
