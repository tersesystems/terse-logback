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

import static net.logstash.logback.argument.StructuredArguments.*;

public class ClassAdviceRewriter {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t|#m|#s") String origin,
                             @Advice.AllArguments Object[] allArguments)
            throws Exception {
        Logger logger = ThreadLocalLogger.getLogger();
        if (logger != null && logger.isInfoEnabled()) {
            String[] args = origin.split("\\|");
            String declaringType = args[0];
            String method = args[1];
            String signature = args[2];
            StructuredArgument aClass = v("class", declaringType);
            StructuredArgument aMethod = v("method", method);
            StructuredArgument aSignature = v("signature", signature);
            StructuredArgument arrayParameters = a("arguments", allArguments);
            logger.trace("entering: {}.{}{} with {}", aClass, aMethod, aSignature, arrayParameters);
        }
    }

    // @Advice.Return Object returnValue

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Origin("#t|#m|#d|#s|#r") String origin, @Advice.AllArguments Object[] allArguments, @Advice.Thrown Throwable thrown) throws Exception {
        Logger logger = ThreadLocalLogger.getLogger();
        if (logger != null && logger.isInfoEnabled()) {
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
                logger.trace("throwing: {}.{}{} with {} ! {}", aClass, aMethod, aSignature, arrayParameters, aThrown);
            } else {
                StructuredArgument arrayParameters = array("arguments", allArguments);
                logger.trace("exiting: {}.{}{} with {} => {}", aClass, aMethod, aSignature, arrayParameters, aReturnType, aDescriptor);
            }
        }
    }
}
