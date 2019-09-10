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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class LoggingInstrumentationAdviceConfig {

    private final List<String> classNames;
    private final ElementMatcher.Junction<? super TypeDescription> typeMatcher;
    private final ElementMatcher.Junction<? super MethodDescription> methodMatcher;

    LoggingInstrumentationAdviceConfig(List<String> classNames,
                                       ElementMatcher.Junction<? super TypeDescription> typeMatcher,
                                       ElementMatcher.Junction<? super MethodDescription> methodMatcher) {
        this.typeMatcher = Objects.requireNonNull(typeMatcher);
        this.methodMatcher = Objects.requireNonNull(methodMatcher);
        this.classNames = classNames;
    }

    public static LoggingInstrumentationAdviceConfig create(String className, List<String> methodNames) throws Exception {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(className);

        ElementMatcher.Junction<? super TypeDescription> types = is(aClass);
        ElementMatcher.Junction<? super MethodDescription> methods = methodMatcher(aClass, methodNames);
        return new LoggingInstrumentationAdviceConfig(singletonList(className), types, methods);
    }

    public List<String> classNames() {
        return this.classNames;
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
        List<String> classNames = new ArrayList<>(classNames());
        classNames.addAll(other.classNames());
        return new LoggingInstrumentationAdviceConfig(classNames, typeMatcher, methodMatcher);
    }

    private static ElementMatcher.Junction<? super MethodDescription> methodMatcher(Class aClass, Collection<String> methodNames) {
        return methodNames.stream().map(m -> named(m).and(isDeclaredBy(aClass)))
                .reduce(none(), ElementMatcher.Junction::or);
    }

}
