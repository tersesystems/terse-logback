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
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassInjector;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;

import static java.util.Collections.singletonMap;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;

public class LogbackInstrumentationAgent extends LogbackInstrumentation {

    private static final Class<?> INSTRUMENTATION_ADVICE_CLASS = LoggingInstrumentationAdvice.class;

    public static void premain(String arg, Instrumentation instrumentation) throws Exception {
        Config config = ConfigFactory.load();
        LogbackInstrumentationAgent agent = new LogbackInstrumentationAgent();
        injectBootstrapClasses(instrumentation);
        agent.initialize(config, instrumentation);
    }

    public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
        Config config = ConfigFactory.load();
        LogbackInstrumentationAgent agent = new LogbackInstrumentationAgent();
        injectBootstrapClasses(instrumentation);
        agent.initialize(config, instrumentation);
    }

    private static void injectBootstrapClasses(Instrumentation instrumentation) throws IOException {
        File tempDir = Files.createTempDirectory("logback-bytebuddy").toFile();
        tempDir.deleteOnExit();

        byte[] classData = read(INSTRUMENTATION_ADVICE_CLASS);
        TypeDescription typeDescription = new TypeDescription.ForLoadedType(INSTRUMENTATION_ADVICE_CLASS);
        ClassInjector classInjector = ClassInjector.UsingInstrumentation.of(tempDir, BOOTSTRAP, instrumentation);
        classInjector.inject(singletonMap(typeDescription, classData));
    }
}
