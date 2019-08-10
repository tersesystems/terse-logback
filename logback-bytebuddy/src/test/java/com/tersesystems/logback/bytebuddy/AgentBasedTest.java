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

import static net.bytebuddy.agent.builder.AgentBuilder.*;

import net.bytebuddy.agent.ByteBuddyAgent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.StringMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class AgentBasedTest {

    // This is a class we're going to redefine completely.
    public static class SomeOtherLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("I am a simple println method with no logging");
        }
    }

    static AgentBuilder.Listener createDebugListener() {
        Listener listener = new Listener.Filtering(
                new StringMatcher("SomeOtherLibraryClass", StringMatcher.Mode.CONTAINS),
                Listener.StreamWriting.toSystemOut());
        return listener;
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        try {
            ClassAdviceConfig config = ClassAdviceConfig.create("SomeOtherLibraryClass", "doesNotUseLogging");

            // The debugging listener shows what classes are being picked up by the instrumentation
            Listener debugListener = createDebugListener();
            new ClassAdviceAgentBuilder().builderFromConfig(config).with(debugListener).installOnByteBuddyAgent();
        } catch (RuntimeException e) {
            System.out.println("Exception instrumenting code : " + e);
            e.printStackTrace();
        }

        Logger logger = LoggerFactory.getLogger(AgentBasedTest.class);
        ThreadLocalLogger.setLogger(logger);

        // No code change necessary here, you can wrap completely in the agent...
        SomeOtherLibraryClass someOtherLibraryClass = new SomeOtherLibraryClass();
        someOtherLibraryClass.doesNotUseLogging();
    }
}
