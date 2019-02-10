package com.tersesystems.logback.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interceptor {

    // @RuntimeType
    // public Object intercept(@SuperCall Callable<?> callable, @AllArguments Object[] allArguments, @Origin Method method, @Origin Class clazz) throws Exception {
    //     System.out.println("INTERCEPTING");
    //     Object response = null;
    //     Logger logger = LoggerFactory.getLogger(clazz);
    //     String methodArg = methodName(clazz, method, allArguments);
    //     try {
    //         if (logger.isTraceEnabled()) {
    //             logger.trace("entering: {}", methodArg);
    //         }
    //         response = callable.call();
    //     } catch (Exception e) {
    //         if (logger.isTraceEnabled()) {
    //             logger.trace("exception: {}", methodArg);
    //         }
    //         throw e;
    //     } finally {
    //         if (logger.isTraceEnabled()) {
    //             logger.trace("exit: {}, response = {}", methodArg, response);
    //         }
    //     }
    //     return response;
    // }

    // private String methodName(Class clazz, Method method) {
    //     return methodName(clazz, method, null);
    // }

    // private String methodName(Class clazz, Method method, Object[] allArguments) {
    //     StringBuilder builder = new StringBuilder();
    //     builder.append(clazz.getName());
    //     builder.append(".");
    //     builder.append(method.getName());
    //     builder.append("(");
    //     Parameter[] parameters = method.getParameters();
    //     for (int i = 0; i < parameters.length; i++) {
    //         builder.append(parameters[i].getName());
    //         if (allArguments != null) {
    //             Object arg = allArguments[i];
    //             builder.append("=");
    //             builder.append(arg != null ? arg.toString() : "null");
    //         }

    //         if (i < parameters.length - 1) {
    //             builder.append(", ");
    //         }
    //     }
    //     builder.append(")");
    //     return builder.toString();
    // }
}