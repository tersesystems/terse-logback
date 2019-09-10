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

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassInjector;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;

import static java.util.Collections.singletonMap;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;

/**
 * The agent class.  This has the magic "premain" and "agentmain" methods for the Java Instrumentation API.
 */
public class LogbackInstrumentationAgent {

    private static final Class<?> INSTRUMENTATION_ADVICE_CLASS = LoggingInstrumentationAdvice.class;

    public static void premain(String arg, Instrumentation instrumentation) throws Exception {
        injectBootstrapClasses(instrumentation);
        LoggingInstrumentationAdvice logbackInst = (LoggingInstrumentationAdvice) INSTRUMENTATION_ADVICE_CLASS.newInstance();

        boolean debug = parseDebug(arg);
        logbackInst.initialize(instrumentation, debug);
    }

    private static boolean parseDebug(String arg) {
        return true;
        //return "debug".equalsIgnoreCase(arg);
    }

    public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
        injectBootstrapClasses(instrumentation);

        boolean debug = parseDebug(arg);
        LoggingInstrumentationAdvice logbackInst = (LoggingInstrumentationAdvice) INSTRUMENTATION_ADVICE_CLASS.newInstance();
        logbackInst.initialize(instrumentation, debug);
    }

    /**
     * Loads the advice class into the bootstrap classloader, so we can access the instantiation.
     *
     * @param instrumentation
     * @throws IOException
     */
    private static void injectBootstrapClasses(Instrumentation instrumentation) throws IOException {
        File tempDir = Files.createTempDirectory("logback-bytebuddy").toFile();
        tempDir.deleteOnExit();

        // Inject the instrumentation advice class directly
        byte[] classData = read(INSTRUMENTATION_ADVICE_CLASS);
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(INSTRUMENTATION_ADVICE_CLASS);
        ClassInjector classInjector = ClassInjector.UsingInstrumentation.of(tempDir, BOOTSTRAP, instrumentation);
        classInjector.inject(singletonMap(typeDescription, classData));
    }

}
