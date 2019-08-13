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
import net.bytebuddy.agent.ByteBuddyAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.tersesystems.logback.bytebuddy.ClassAdviceUtils.createDebugListener;
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

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        Logger logger = LoggerFactory.getLogger(InProcessInstrumentationExample.class);
        LoggingInstrumentationAdvice.setLoggerResolver(new FixedLoggerResolver(logger));

        Config config = ConfigFactory.load();
        List<String> classNames = config.getStringList("logback.bytebuddy.classNames");
        List<String> methodNames = config.getStringList("logback.bytebuddy.methodNames");
        LoggingAdviceConfig loggingAdviceConfig = LoggingAdviceConfig.create(classNames, methodNames);

        // The debugging listener shows what classes are being picked up by the instrumentation
        Listener debugListener = createDebugListener(classNames);
        new LoggingInstrumentationByteBuddyBuilder()
                .builderFromConfig(loggingAdviceConfig)
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
