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

import static net.logstash.logback.argument.StructuredArguments.*;

public class InfoLoggingInterceptor {

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable, @AllArguments Object[] allArguments, @Origin Method method, @Origin Class clazz) throws Exception {
        Object response = null;

        Logger logger = ThreadLocalLogger.getLogger();
        try {
            if (logger != null && logger.isInfoEnabled()) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.info("entering: {}.{}({})", aClass, aMethod, e(parameters));
                } else {
                    logger.info("entering: {}.{}()", aClass, aMethod);
                }
            }
            response = callable.call();
        } catch (Exception e) {
            if (logger != null && logger.isInfoEnabled()) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                StructuredArgument aException = v("throwable", e);
                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.info("exception: {}.{}({}) ! {}", aClass, aMethod, e(parameters), aException);
                } else {
                    logger.info("exception: {}.{}() ! {}", aClass, aMethod, aException);
                }
            }
            throw e;
        } finally {
            if (logger != null && logger.isInfoEnabled()) {
                StructuredArgument aClass = v("class", clazz.getName());
                StructuredArgument aMethod = v("method", method.getName());
                StructuredArgument aResponse = a("response", response);

                Map<String, Object> parameters = parameters(method, allArguments);
                if (! parameters.isEmpty()) {
                    logger.info("exit: {}.{}({}) => {}", aClass, aMethod, e(parameters), aResponse);
                } else {
                    logger.info("exit: {}.{}() => {}", aClass, aMethod, aResponse);
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