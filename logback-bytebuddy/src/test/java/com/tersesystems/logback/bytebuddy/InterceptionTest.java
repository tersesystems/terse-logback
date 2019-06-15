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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptionTest {

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
                    .intercept(MethodDelegation.to(new TraceLoggingInterceptor()))
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

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        Logger logger = LoggerFactory.getLogger(InterceptionTest.class);
        ThreadLocalLogger.setLogger(logger);

        new Interception().doStuff();
    }
}
