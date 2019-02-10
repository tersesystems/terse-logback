package com.tersesystems.logback.bytebuddy;

import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassAdviceRewriter {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t|#m|#d|#s|#r") String origin,
                             @Advice.AllArguments Object[] allArguments)
            throws Exception {
        String[] args = origin.split("\\|");
        String declaringType = args[0];
        String method = args[1];
        String descriptor = args[2];
        String signature = args[3];
        String returnType = args[4];
        Logger logger = LoggerFactory.getLogger(declaringType);
        String methodArg = methodName(declaringType, method, allArguments);
        if (logger.isInfoEnabled()) {
            logger.info("entering: {}", methodArg);
        }
    }

    @Advice.OnMethodExit
    public static void exit(@Advice.Origin("#t|#m|#d|#s|#r") String origin, @Advice.AllArguments Object[] allArguments) throws Exception {
        String[] args = origin.split("\\|");
        String declaringType = args[0];
        String method = args[1];
        String descriptor = args[2];
        String signature = args[3];
        String returnType = args[4];

        Logger logger = LoggerFactory.getLogger(declaringType);
        String methodArg = methodName(declaringType, method, allArguments);
        if (logger.isInfoEnabled()) {
            logger.info("exiting: {}", methodArg);
        }
    }

    public static String methodName(String declaringType, String method, Object[] allArguments) {
        StringBuilder builder = new StringBuilder();
        builder.append(declaringType);
        builder.append(".");
        builder.append(method);
        builder.append("(");
        for (int i = 0; i < allArguments.length; i++) {
            builder.append(allArguments[i].toString());
            if (allArguments != null) {
                Object arg = allArguments[i];
                builder.append("=");
                builder.append(arg != null ? arg.toString() : "null");
            }

            if (i < allArguments.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
