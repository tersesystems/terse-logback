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
import com.tersesystems.logback.tracing.SpanInfo;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Optional;

import static com.tersesystems.logback.bytebuddy.impl.SystemFlow.*;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

public class Exit {

    private static String exitFormatWithSource = "exiting: {}.{}{} with {} => ({} {}) from source {}:{}";
    private static String exitFormat = "exiting: {}.{}{} with {} => ({} {})";

    public static void apply(String origin, Object[] allArguments, Throwable thrown, Object returnValue) {
        Logger logger = getLogger(origin);
        if (logger != null && logger.isTraceEnabled(EXIT_MARKER)) {
            Optional<SpanInfo> span = popSpan();

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
                        ? createMarker(span.get()).and(EXCEPTION_MARKER)
                        : EXCEPTION_MARKER;
                // Always include the thrown at the end of the list as SLF4J will take care of stack trace.
                logger.error(markers, "throwing: {}.{}{} with {}", aClass, aMethod, aSignature, safeArguments, thrown);
            } else {
                StructuredArgument aReturnType = kv("return_type", returnType);
                StructuredArgument safeReturnValue = safeReturnValue(returnValue);

                MethodInfoLookup lookup = MethodInfoLookup.getInstance();
                Optional<MethodInfo> methodInfo = lookup.find(declaringType, method, descriptor);
                Marker markers = (span.isPresent())
                        ? createMarker(span.get()).and(EXIT_MARKER)
                        : EXIT_MARKER;
                if (methodInfo.isPresent()) {
                    MethodInfo mi = methodInfo.get();
                    StructuredArgument aSource = v("source", mi.source);
                    StructuredArgument aLineNumber = v("line", mi.getEndLine());
                    ;
                    logger.trace(markers, exitFormatWithSource, aClass, aMethod, aSignature, safeArguments, aReturnType, safeReturnValue, aSource, aLineNumber);
                } else {
                    logger.trace(markers, exitFormat, aClass, aMethod, aSignature, safeArguments, aReturnType, safeReturnValue);
                }
            }
        }
    }

}
