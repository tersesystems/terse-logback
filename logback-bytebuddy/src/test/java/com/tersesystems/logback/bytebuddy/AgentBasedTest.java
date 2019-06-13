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
import net.bytebuddy.matcher.ElementMatchers;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.StringMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class AgentBasedTest {

    // This is a class we're going to redefine completely.
    public static class SomeOtherLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("I agree, I don't use logging either");
        }
    }

    static class AgentBased {
        public static void premain() {
            try {
                String className = "SomeOtherLibraryClass";
                String methodName = "doesNotUseLogging";

                // The debugging listener shows what classes are being picked up by the instrumentation
                Listener.Filtering debuggingListener = new Listener.Filtering(
                        new StringMatcher(className, StringMatcher.Mode.CONTAINS),
                        Listener.StreamWriting.toSystemOut());

                // This gives a bit of a speedup when going through classes...
                RawMatcher ignoreMatcher = new RawMatcher.ForElementMatchers(ElementMatchers.nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any());

                // Create and install the byte buddy remapper
                new AgentBuilder.Default()
                        .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                        .ignore(ignoreMatcher)
                        .with(debuggingListener)
                        .type(ElementMatchers.nameContains(className))
                        .transform((builder, type, classLoader, module) ->
                                builder.visit(Advice.to(ClassAdviceRewriter.class).on(named(methodName)))
                        )
                        .installOnByteBuddyAgent();
            } catch (RuntimeException e) {
                System.out.println("Exception instrumenting code : " + e);
                e.printStackTrace();
            }
        };

        public void doStuff() {
            // No code change necessary here, you can wrap completely in the agent...
            SomeOtherLibraryClass someOtherLibraryClass = new SomeOtherLibraryClass();
            someOtherLibraryClass.doesNotUseLogging();
        }
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();
        AgentBased.premain();

        Logger logger = LoggerFactory.getLogger(AgentBasedTest.class);
        ThreadLocalLogger.setLogger(logger);

        new AgentBased().doStuff();
    }
}
