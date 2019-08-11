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
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class ClassAdviceAgentBuilder {

    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher) {
        return new AgentBuilder.Default()
                .disableClassFormatChanges()
                .type(typesMatcher)
                .transform((builder, type, classLoader, module) ->
                        builder.visit(Advice.to(ClassAdviceRewriter.class).on(methodsMatcher))
                );
    }

    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher,
                                          AgentBuilder.Listener listener) {
        return builderFromConfig(typesMatcher, methodsMatcher).with(listener);
    }

    public AgentBuilder builderFromConfig(ClassAdviceConfig c) {
        return builderFromConfig(c.types(), c.methods());
    }
}
