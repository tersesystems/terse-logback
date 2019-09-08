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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.StringMatcher;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * The code to be added on entry / exit to the methods under instrumentation.
 */
public class LoggingInstrumentationAdvice {

    private static final String LOGBACK = "logback";

    private static final String LOGBACK_TEST = "logback-test";

    private static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

    private static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

    // We need to load the implementation of enter / exit methods from the system classloader,
    // so that we don't end up hauling SLF4J impl factory into bootstrap classloader, which
    // will hopelessly confuse the JVM.
    public static Method enterMethod;

    static {
        try {
            String className = "com.tersesystems.logback.bytebuddy.impl.Enter";
            Class<?> enterClass = systemClassLoader.loadClass(className);
            enterMethod = enterClass.getMethod("apply", String.class, Object[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Method exitMethod;
    static {
        try {
            String className = "com.tersesystems.logback.bytebuddy.impl.Exit";
            Class<?> exitClass = systemClassLoader.loadClass(className);
            exitMethod = exitClass.getMethod("apply", String.class, Object[].class, Throwable.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // The code here recapitulates the logback-config code, but in a bootstrap classloader.
    // This does mean that typesafe-config classes are pulled from bootstrap thereafter, but
    // this is pretty safe.
    private Config generateConfig(ClassLoader classLoader, boolean debug) {
        // Look for logback.json, logback.conf, logback.properties
        Config systemProperties = ConfigFactory.systemProperties();
        String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
        Config file = ConfigFactory.empty();
        if (fileName != null) {
            file = ConfigFactory.parseFile(new File(fileName));
        }

        Config testResources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK_TEST);
        Config resources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK);
        Config reference = ConfigFactory.parseResources(classLoader, LOGBACK_REFERENCE_CONF);

        Config config = systemProperties        // Look for a property from system properties first...
                .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
                .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
                .withFallback(resources)     // then look in logback.conf...
                .withFallback(reference)     // and then finally in logback-reference.conf.
                .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.

        // Add a check to show the config value if nothing is working...
        if (debug) {
            String configString = config.root().render(ConfigRenderOptions.defaults());
            System.out.println(configString);
        }
        return config;
    }

    void initialize(Instrumentation instrumentation, boolean debug) {
        try {
            Config config = generateConfig(this.getClass().getClassLoader(), debug);
            List<String> classNames = getClassNames(config);
            List<String> methodNames = getMethodNames(config);
            LoggingInstrumentationAdviceConfig loggingInstrumentationAdviceConfig = LoggingInstrumentationAdviceConfig.create(classNames, methodNames);
            AgentBuilder agentBuilder = new LoggingInstrumentationByteBuddyBuilder()
                    .builderFromConfigWithRetransformation(loggingInstrumentationAdviceConfig);

            // The debugging listener shows what classes are being picked up by the instrumentation
            if (debug) {
                AgentBuilder.Listener debugListener = createDebugListener(classNames);
                agentBuilder = agentBuilder.with(debugListener);
            }
            agentBuilder.installOn(instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return new AgentBuilder.Listener.Filtering(
                stringMatcher(classNames),
                AgentBuilder.Listener.StreamWriting.toSystemOut());
    }

    public static ElementMatcher.Junction<? super String> stringMatcher(Collection<String> typeNames) {
        boolean seen = false;
        ElementMatcher.Junction<? super String> acc = ElementMatchers.none();
        for (String typeName : typeNames) {
            StringMatcher stringMatcher = new StringMatcher(typeName, StringMatcher.Mode.EQUALS_FULLY);
            if (!seen) {
                seen = true;
                acc = stringMatcher;
            } else {
                acc = acc.or(stringMatcher);
            }
        }
        return acc;
    }

    private List<String> getMethodNames(Config config) {
        return config.getStringList("logback.bytebuddy.methodNames");
    }

    private List<String> getClassNames(Config config) {
        return config.getStringList("logback.bytebuddy.classNames");
    }

    @Advice.OnMethodEnter
    public static void enter(@Advice.Origin("#t|#m|#d|#s") String origin,
                             @Advice.AllArguments Object[] allArguments)
            throws Exception {
        enterMethod.invoke(null, origin, allArguments);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.Origin("#t|#m|#d|#s|#r") String origin, @Advice.AllArguments Object[] allArguments, @Advice.Thrown Throwable thrown) throws Exception {
        exitMethod.invoke(null, origin, allArguments, thrown);
    }
}
