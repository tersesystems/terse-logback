package com.tersesystems.logback.bytebuddy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;

public class InfoLoggingInterceptor {


    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable, @AllArguments Object[] allArguments, @Origin Method method, @Origin Class clazz) throws Exception {
        Object response = null;

        Logger logger = ThreadLocalLogger.getLogger();
        String methodArg = methodName(clazz, method, allArguments);
        try {
            if (logger != null && logger.isInfoEnabled()) {
                logger.info("entering: {}", methodArg);
            }
            response = callable.call();
        } catch (Exception e) {
            if (logger != null && logger.isInfoEnabled()) {
                logger.info("exception: {}", methodArg);
            }
            throw e;
        } finally {
            if (logger != null && logger.isInfoEnabled()) {
                logger.info("exit: {}, response = {}", methodArg, response);
            }
        }
        return response;
    }

    private String methodName(Class clazz, Method method) {
        return methodName(clazz, method, null);
    }

    private String methodName(Class clazz, Method method, Object[] allArguments) {
        StringBuilder builder = new StringBuilder();
        builder.append(clazz.getName());
        builder.append(".");
        builder.append(method.getName());
        builder.append("(");
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            builder.append(parameters[i].getName());
            if (allArguments != null) {
                Object arg = allArguments[i];
                builder.append("=");
                builder.append(arg != null ? arg.toString() : "null");
            }

            if (i < parameters.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}