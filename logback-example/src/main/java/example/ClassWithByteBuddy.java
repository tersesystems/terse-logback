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
package example;

import com.tersesystems.logback.bytebuddy.ClassAdviceRewriter;
import com.tersesystems.logback.bytebuddy.InfoLoggingInterceptor;
import com.tersesystems.logback.bytebuddy.ThreadLocalLogger;
import net.bytebuddy.ByteBuddy;
import static net.bytebuddy.agent.builder.AgentBuilder.*;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.StringMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class ClassWithByteBuddy {

    // This is a class we're going to wrap entry and exit methods around.
    public static class SomeLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("Logging sucks, I use println");
        }
    }

    // We can do this by intercepting the class and putting stuff around it.
    static class Interception {
        // Do it through wrapping
        public SomeLibraryClass instrumentClass() throws IllegalAccessException, InstantiationException {
            Class<SomeLibraryClass> offendingClass = SomeLibraryClass.class;
            String offendingMethodName = "doesNotUseLogging";

            return new ByteBuddy()
                    .subclass(offendingClass)
                    .method(ElementMatchers.named(offendingMethodName))
                    .intercept(MethodDelegation.to(new InfoLoggingInterceptor()))
                    .make()
                    .load(offendingClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        }

        public void doStuff() throws IllegalAccessException, InstantiationException {
            SomeLibraryClass someLibraryClass = this.instrumentClass();
            someLibraryClass.doesNotUseLogging();
        }
    }

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

        Logger logger = LoggerFactory.getLogger(ClassWithByteBuddy.class);
        ThreadLocalLogger.setLogger(logger);

        new Interception().doStuff();
        new AgentBased().doStuff();
    }
}
