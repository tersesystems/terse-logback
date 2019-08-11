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
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class AgentBasedTest {

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();
        Config config = ConfigFactory.load();
        List<String> classNames = config.getStringList("bytebuddy.classNames");
        List<String> methodNames = config.getStringList("bytebuddy.methodNames");
        ClassAdviceConfig classAdviceConfig = ClassAdviceConfig.create(classNames, methodNames);

        // The debugging listener shows what classes are being picked up by the instrumentation
        Listener debugListener = createDebugListener(classNames);
        new ClassAdviceAgentBuilder()
                .builderFromConfig(classAdviceConfig)
                .with(debugListener)
                .installOnByteBuddyAgent();

        Logger logger = LoggerFactory.getLogger(AgentBasedTest.class);
        ThreadLocalLogger.setLogger(logger);

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
