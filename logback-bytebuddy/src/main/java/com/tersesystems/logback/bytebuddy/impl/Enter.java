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
package com.tersesystems.logback.bytebuddy.impl;

import com.tersesystems.logback.bytebuddy.MethodInfo;
import com.tersesystems.logback.bytebuddy.MethodInfoLookup;

import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.a;
import static net.logstash.logback.argument.StructuredArguments.v;

import static com.tersesystems.logback.bytebuddy.impl.SystemFlow.*;
import static net.logstash.logback.marker.Markers.aggregate;

public class Enter {

    public static void apply(String origin, Object[] allArguments) {
        Logger logger = getLogger(origin);
        if (logger != null && logger.isTraceEnabled(ENTRY_MARKER)) {

            String[] args = origin.split("\\|");
            String declaringType = args[0];
            String method = args[1];
            String descriptor = args[2];
            String signature = args[3];
            StructuredArgument aClass = v("class", declaringType);
            StructuredArgument aMethod = v("method", method);
            StructuredArgument aSignature = v("signature", signature);
            StructuredArgument arrayParameters = a("arguments", allArguments);

            String name = createName(declaringType, method, signature);
            Tracer.pushSpan(name);
            Marker markers = ENTRY_MARKER;

            MethodInfoLookup lookup = MethodInfoLookup.getInstance();
            Optional<MethodInfo> methodInfo = lookup.find(declaringType, method, descriptor);
            if (methodInfo.isPresent()) {
                MethodInfo mi = methodInfo.get();
                StructuredArgument aSource = v("source", mi.source);
                StructuredArgument aLineNumber = v("line", mi.getStartLine());
                logger.trace(markers, "entering: {}.{}{} with {} from source {}:{}", aClass, aMethod, aSignature, arrayParameters, aSource, aLineNumber);
            } else {
                logger.trace(markers, "entering: {}.{}{} with {}", aClass, aMethod, aSignature, arrayParameters);
            }
        }
    }

    private static String createName(String className, String method, String signature) {
        return className + "." + method + signature;
    }

}
