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

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class AdviceConfig {

    private final List<String> classNames;
    private final ElementMatcher.Junction<? super TypeDescription> typeMatcher;
    private final ElementMatcher.Junction<? super MethodDescription> methodMatcher;

    private AdviceConfig(List<String> classNames,
                 ElementMatcher.Junction<? super TypeDescription> typeMatcher,
                 ElementMatcher.Junction<? super MethodDescription> methodMatcher) {
        this.typeMatcher = Objects.requireNonNull(typeMatcher);
        this.methodMatcher = Objects.requireNonNull(methodMatcher);
        this.classNames = classNames;
    }

    public static AdviceConfig create(String className, List<String> methodNames) throws Exception {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(className);
        return new AdviceConfig(singletonList(className), is(aClass), methodNames.stream()
                .map(m -> named(m).and(isDeclaredBy(aClass)))
                .reduce(none(), ElementMatcher.Junction::or));
    }

    public static AdviceConfig create(String className) throws Exception {
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass(className);
        // We don't want to get the constructor, so get a list of all methods from the class.
        Method[] methods = aClass.getDeclaredMethods();
        return new AdviceConfig(singletonList(className), is(aClass), ElementMatchers.anyOf(methods));
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

    public AdviceConfig join(AdviceConfig other) {
        final ElementMatcher.Junction<? super MethodDescription> methodMatcher = methods().or(other.methods());
        final ElementMatcher.Junction<? super TypeDescription> typeMatcher = types().or(other.types());
        List<String> classNames = new ArrayList<>(classNames());
        classNames.addAll(other.classNames());
        return new AdviceConfig(classNames, typeMatcher, methodMatcher);
    }

}
