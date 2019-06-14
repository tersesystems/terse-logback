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
package com.tersesystems.logback.bytebuddy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static net.logstash.logback.argument.StructuredArguments.*;

public class TraceLoggingInterceptor {
    // https://github.com/qos-ch/slf4j/blob/master/slf4j-ext/src/main/java/org/slf4j/ext/XLogger.java#L44
    static Marker FLOW_MARKER = MarkerFactory.getMarker("FLOW");
    static Marker ENTRY_MARKER = MarkerFactory.getMarker("ENTRY");
    static Marker EXIT_MARKER = MarkerFactory.getMarker("EXIT");
    static Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");

    static {
        ENTRY_MARKER.add(FLOW_MARKER);
        EXIT_MARKER.add(FLOW_MARKER);
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable, @AllArguments Object[] allArguments, @Origin Method method, @Origin Class clazz) throws Exception {
        Object response = null;

        Logger logger = ThreadLocalLogger.getLogger();
        try {
            if (logger != null && logger.isTraceEnabled(ENTRY_MARKER)) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.trace(ENTRY_MARKER, "entering: {}.{}({})", aClass, aMethod, e(parameters));
                } else {
                    logger.trace(ENTRY_MARKER, "entering: {}.{}()", aClass, aMethod);
                }
            }
            response = callable.call();
        } catch (Exception e) {
            if (logger != null && logger.isErrorEnabled(EXCEPTION_MARKER)) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                StructuredArgument aException = v("throwable", e);
                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.error(EXCEPTION_MARKER,"exception: {}.{}({}) ! {}", aClass, aMethod, e(parameters), aException);
                } else {
                    logger.error(EXCEPTION_MARKER, "exception: {}.{}() ! {}", aClass, aMethod, aException);
                }
            }
            throw e;
        } finally {
            if (logger != null && logger.isTraceEnabled(EXIT_MARKER)) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                StructuredArgument aResponse = a("response", response);

                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.trace(EXIT_MARKER, "exit: {}.{}({}) => {}", aClass, aMethod, e(parameters), aResponse);
                } else {
                    logger.trace(EXIT_MARKER, "exit: {}.{}() => {}", aClass, aMethod, aResponse);
                }
            }
        }
        return response;
    }

    private Map<String, Object> parameters(Method method, Object[] allArguments) {
        Map<String, Object> parametersMap = new LinkedHashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (allArguments != null) {
                parametersMap.put(parameters[i].getName(), allArguments[i]);
            }
        }
        return parametersMap;
    }
}