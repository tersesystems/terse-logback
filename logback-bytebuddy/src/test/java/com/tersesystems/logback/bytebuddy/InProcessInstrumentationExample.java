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

import com.tersesystems.logback.bytebuddy.impl.FixedLoggerResolver;
import com.tersesystems.logback.bytebuddy.impl.SystemFlow;
import com.typesafe.config.Config;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.bytebuddy.agent.builder.AgentBuilder.Listener;

/**
 * Run the agent inside an already running JVM.
 *
 * This will instrument classes that have not already been loaded into the JVM, such as ClassCalledByAgent,
 * but will not allow you to instrument classes loaded by the system classloader, such as java.lang.Thread.
 *
 * This should still be perfectly fine for 99% of users who don't need an agent loaded from the command line.
 */
public class InProcessInstrumentationExample {

    public static AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return new AgentBuilder.Listener.Filtering(
                LoggingInstrumentationAdvice.stringMatcher(classNames),
                AgentBuilder.Listener.StreamWriting.toSystemOut());
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        Logger logger = LoggerFactory.getLogger(InProcessInstrumentationExample.class);
        SystemFlow.setLoggerResolver(new FixedLoggerResolver(logger));

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Config config = LoggingInstrumentationAdvice.generateConfig(classLoader, false);
        AdviceConfig adviceConfig = LoggingInstrumentationAdvice.generateAdviceConfig(classLoader, config, false);

        // The debugging listener shows what classes are being picked up by the instrumentation
        Listener debugListener = createDebugListener(adviceConfig.classNames());
        new LoggingInstrumentationByteBuddyBuilder()
                .builderFromConfig(adviceConfig)
                .with(debugListener)
                .installOnByteBuddyAgent();

        // No code change necessary here, you can wrap completely in the agent...
        ClassCalledByAgent classCalledByAgent = new ClassCalledByAgent();
        classCalledByAgent.printStatement();
        classCalledByAgent.printArgument("42");

        try {
            classCalledByAgent.throwException("hello world");
        } catch (Exception e) {
            // I am too lazy to catch this exception.  I hope someone does it for me.
        }
    }
}
