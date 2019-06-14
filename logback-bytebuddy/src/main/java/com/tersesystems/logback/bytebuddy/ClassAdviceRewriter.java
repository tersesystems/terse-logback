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

import net.bytebuddy.asm.Advice;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static net.logstash.logback.argument.StructuredArguments.*;

public class ClassAdviceRewriter {
    // https://github.com/qos-ch/slf4j/blob/master/slf4j-ext/src/main/java/org/slf4j/ext/XLogger.java#L44
    static Marker FLOW_MARKER = MarkerFactory.getMarker("FLOW");
    static Marker ENTRY_MARKER = MarkerFactory.getMarker("ENTRY");
    static Marker EXIT_MARKER = MarkerFactory.getMarker("EXIT");
    static Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");

    static {
        ENTRY_MARKER.add(FLOW_MARKER);
        EXIT_MARKER.add(FLOW_MARKER);
    }


    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t|#m|#s") String origin,
                             @Advice.AllArguments Object[] allArguments)
            throws Exception {
        Logger logger = ThreadLocalLogger.getLogger();
        if (logger != null && logger.isTraceEnabled(ENTRY_MARKER)) {
            String[] args = origin.split("\\|");
            String declaringType = args[0];
            String method = args[1];
            String signature = args[2];
            StructuredArgument aClass = v("class", declaringType);
            StructuredArgument aMethod = v("method", method);
            StructuredArgument aSignature = v("signature", signature);
            StructuredArgument arrayParameters = a("arguments", allArguments);
            logger.trace(ENTRY_MARKER, "entering: {}.{}{} with {}", aClass, aMethod, aSignature, arrayParameters);
        }
    }

    // @Advice.Return Object returnValue

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Origin("#t|#m|#d|#s|#r") String origin, @Advice.AllArguments Object[] allArguments, @Advice.Thrown Throwable thrown) throws Exception {
        Logger logger = ThreadLocalLogger.getLogger();
        if (logger != null && logger.isTraceEnabled(EXIT_MARKER)) {
            String[] args = origin.split("\\|");
            String declaringType = args[0];
            String method = args[1];
            String descriptor = args[2];
            String signature = args[3];
            String returnType = args[4];
            StructuredArgument aClass = v("class", declaringType);
            StructuredArgument aMethod = v("method", method);
            StructuredArgument aSignature = v("signature", signature);
            StructuredArgument aDescriptor = kv("descriptor", descriptor);
            StructuredArgument aReturnType = kv("returnType", returnType);
            if (thrown != null) {
                StructuredArgument aThrown = kv("thrown", thrown);
                StructuredArgument arrayParameters = array("arguments", allArguments);
                logger.error(EXCEPTION_MARKER, "throwing: {}.{}{} with {} ! {}", aClass, aMethod, aSignature, arrayParameters, aThrown);
            } else {
                StructuredArgument arrayParameters = array("arguments", allArguments);
                logger.trace(EXIT_MARKER, "exiting: {}.{}{} with {} => {}", aClass, aMethod, aSignature, arrayParameters, aReturnType, aDescriptor);
            }
        }
    }
}
