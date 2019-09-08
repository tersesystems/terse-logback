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

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.util.Collections.singleton;

public class LoggingInstrumentationAdviceConfig {
    private final ElementMatcher.Junction<? super TypeDescription> typeMatcher;
    private final ElementMatcher.Junction<? super MethodDescription> methodMatcher;

    public LoggingInstrumentationAdviceConfig(ElementMatcher.Junction<? super TypeDescription> typeMatcher,
                                              ElementMatcher.Junction<? super MethodDescription> methodMatcher) {
        this.typeMatcher = Objects.requireNonNull(typeMatcher);
        this.methodMatcher = Objects.requireNonNull(methodMatcher);
    }

    public static LoggingInstrumentationAdviceConfig create(String className, String methodName) {
        return new LoggingInstrumentationAdviceConfig(typeMatcher(singleton(className)), methodMatcher(singleton(methodName)));
    }

    public static LoggingInstrumentationAdviceConfig create(String className, String... methodNames) {
        return new LoggingInstrumentationAdviceConfig(typeMatcher(singleton(className)), methodMatcher(Arrays.asList(methodNames)));
    }

    public static LoggingInstrumentationAdviceConfig create(Collection<String> typeNames, Collection<String> methodNames) {
        return new LoggingInstrumentationAdviceConfig(typeMatcher(typeNames), methodMatcher(methodNames));
    }

    public ElementMatcher.Junction<? super MethodDescription> methods() {
        return methodMatcher;
    }

    public ElementMatcher.Junction<? super TypeDescription> types() {
        return typeMatcher;
    }

    public LoggingInstrumentationAdviceConfig join(LoggingInstrumentationAdviceConfig other) {
        final ElementMatcher.Junction<? super MethodDescription> methodMatcher = methods().or(other.methods());
        final ElementMatcher.Junction<? super TypeDescription> typeMatcher = types().or(other.types());
        return new LoggingInstrumentationAdviceConfig(typeMatcher, methodMatcher);
    }

    public static ElementMatcher.Junction<? super MethodDescription> methodMatcher(Collection<String> methodNames) {
        return methodNames.stream().map(ElementMatchers::named).reduce(ElementMatchers.none(), ElementMatcher.Junction::or);
    }

    public static ElementMatcher.Junction<? super TypeDescription> typeMatcher(Collection<String> typeNames) {
        return typeNames.stream().map(ElementMatchers::nameContains).reduce(ElementMatchers.none(), ElementMatcher.Junction::or);
    }

}
