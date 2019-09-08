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

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.*;

import static com.tersesystems.logback.bytebuddy.impl.SystemFlow.*;

public class Exit {

    public static void apply(String origin, Object[] allArguments, Throwable thrown) {
        Logger logger = getLogger(origin);
        if (logger != null && logger.isTraceEnabled(EXIT_MARKER)) {
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
            StructuredArgument aReturnType = kv("returnType", returnType); // returnType=void
            if (thrown != null) {
                StructuredArgument aThrown = kv("thrown", thrown);
                StructuredArgument arrayParameters = array("arguments", allArguments);
                // Always include the thrown at the end of the list as SLF4J will take care of stack trace.
                logger.error(EXCEPTION_MARKER, "throwing: {}.{}{} with {} ! {}", aClass, aMethod, aSignature, arrayParameters, aThrown, thrown);
            } else {
                StructuredArgument arrayParameters = array("arguments", allArguments);

                MethodInfoLookup lookup = MethodInfoLookup.getInstance();
                Optional<MethodInfo> methodInfo = lookup.find(declaringType, method, descriptor);
                if (methodInfo.isPresent()) {
                    MethodInfo mi = methodInfo.get();
                    StructuredArgument aSource = v("source", mi.source);
                    StructuredArgument aLineNumber = v("line", mi.getEndLine());
                    logger.trace(EXIT_MARKER, "exiting: {}.{}{} with {} => {} from source {}:{}", aClass, aMethod, aSignature, arrayParameters, aReturnType, aSource, aLineNumber);
                } else {
                    logger.trace(EXIT_MARKER, "exiting: {}.{}{} with {} => {}", aClass, aMethod, aSignature, arrayParameters, aReturnType);
                }
            }
        }
    }
}
