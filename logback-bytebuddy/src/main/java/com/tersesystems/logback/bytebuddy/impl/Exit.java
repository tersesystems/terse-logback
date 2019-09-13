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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.*;

import static com.tersesystems.logback.bytebuddy.impl.SystemFlow.*;

public class Exit {

    public static void apply(String origin, Object[] allArguments, Throwable thrown, Object returnValue) {
        Logger logger = getLogger(origin);
        if (logger != null && logger.isTraceEnabled(EXIT_MARKER)) {
            Optional<Tracer.Span> span = Tracer.popSpan();

            String[] args = origin.split("\\|");
            String declaringType = args[0];
            String method = args[1];
            String descriptor = args[2];
            String signature = args[3];
            String returnType = args[4];
            StructuredArgument aClass = v("class", declaringType); // ClassCalledByAgent
            StructuredArgument aMethod = v("method", method); // printArgument
            StructuredArgument aSignature = v("signature", signature); // (java.lang.String)
            //StructuredArgument aDescriptor = kv("descriptor", descriptor); // descriptor=(Ljava/lang/String;)V

            StructuredArgument safeArguments = safeArguments(allArguments);

            if (thrown != null) {
                Marker markers = (span.isPresent())
                        ? Tracer.createExitMarkers(span.get(), EXCEPTION_MARKER)
                        : EXCEPTION_MARKER;
                // Always include the thrown at the end of the list as SLF4J will take care of stack trace.
                logger.error(markers, "throwing: {}.{}{} with {}", aClass, aMethod, aSignature, safeArguments, thrown);
            } else {
                StructuredArgument aReturnType = kv("returnType", returnType);
                StructuredArgument safeReturnValue = safeReturnValue(returnType);

                MethodInfoLookup lookup = MethodInfoLookup.getInstance();
                Optional<MethodInfo> methodInfo = lookup.find(declaringType, method, descriptor);
                Marker markers = (span.isPresent())
                        ? Tracer.createExitMarkers(span.get(), EXIT_MARKER)
                        : EXIT_MARKER;
                if (methodInfo.isPresent()) {
                    MethodInfo mi = methodInfo.get();
                    StructuredArgument aSource = v("source", mi.source);
                    StructuredArgument aLineNumber = v("line", mi.getEndLine());
                    logger.trace(markers, "exiting: {}.{}{} with {} => ({} {}) from source {}:{}", aClass, aMethod, aSignature, safeArguments, aReturnType, safeReturnValue, aSource, aLineNumber);
                } else {
                    logger.trace(markers, "exiting: {}.{}{} with {} => ({} {})", aClass, aMethod, aSignature, safeArguments, aReturnType, safeReturnValue);
                }
            }
        }
    }

    // objects in general cannot be passed to StructuredArgument, because there is a contract
    // that a StructuredArgument is safe for JSON serialization, and we could do nasty
    // things like call sslContext.getSocketFactory() which can throw an exception.
    private static StructuredArgument safeReturnValue(String returnType) {
        String safeReturnValue = Objects.toString(returnType);
        return kv("returnValue", safeReturnValue);
    }

    private static StructuredArgument safeArguments(Object[] allArguments) {
        List<String> safeArgs = Arrays.stream(allArguments).map(Objects::toString).collect(Collectors.toList());
        return kv("arguments", safeArgs);
    }
}
