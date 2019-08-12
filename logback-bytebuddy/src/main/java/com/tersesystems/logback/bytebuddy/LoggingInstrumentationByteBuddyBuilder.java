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

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.any;

public class LoggingInstrumentationByteBuddyBuilder {

    /**
     * Creates a builder using
     *
     * @param typesMatcher an element matcher for types we should instrument.
     * @param methodsMatcher an element matcher for the methods in the types that should be instrumented.
     * @return
     */
    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher) {
        return new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .disableClassFormatChanges() // frozen instrumented types
                .type(typesMatcher) // for these classes...
                .transform((builder, type, classLoader, module) ->
                        // ...apply this advice to these methods.
                        builder.visit(Advice.to(LoggingInstrumentationAdvice.class).on(methodsMatcher))
                );
    }

    /**
     * Use this method if you want to redefine system classloader classes.
     *
     * @param typesMatcher an element matcher for types we should instrument.
     * @param methodsMatcher an element matcher for the methods in the types that should be instrumented.
     * @return agent builder with ignore and RETRANSFORMATION set.
     */
    public AgentBuilder builderFromConfigWithRetransformation(ElementMatcher<? super TypeDescription> typesMatcher,
                                                              ElementMatcher<? super MethodDescription> methodsMatcher) {
        return withSystemClassLoaderMatching(builderFromConfig(typesMatcher, methodsMatcher));
    }

    protected AgentBuilder withSystemClassLoaderMatching(AgentBuilder builder) {
        return builder
                .ignore(ignoreMatchers())                                 // do not ignore system classes
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION); // try to retransform already loaded classes
    }

    public AgentBuilder.RawMatcher.ForElementMatchers ignoreMatchers() {
        ElementMatcher.Junction<? super TypeDescription> matchers =
                nameStartsWith("net.bytebuddy.")
                //.or(nameStartsWith("com.tersesystems.logback.bytebuddy"))
                .or(nameStartsWith("org.slf4j."))
                .or(nameStartsWith("ch.qos.logback."))
                .or(isSynthetic());
        return new AgentBuilder.RawMatcher.ForElementMatchers(matchers, any(), any());
    }

    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher,
                                          AgentBuilder.Listener listener) {
        return builderFromConfig(typesMatcher, methodsMatcher).with(listener);
    }

    public AgentBuilder builderFromConfig(ClassAdviceConfig c) {
        return builderFromConfig(c.types(), c.methods());
    }

    public AgentBuilder builderFromConfigWithRetransformation(ClassAdviceConfig c) {
        return builderFromConfigWithRetransformation(c.types(), c.methods());
    }
}
