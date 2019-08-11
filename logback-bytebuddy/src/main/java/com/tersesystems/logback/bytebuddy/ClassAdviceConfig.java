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

import java.util.Arrays;
import java.util.Collection;

import static com.tersesystems.logback.bytebuddy.ClassAdviceUtils.methodMatcher;
import static com.tersesystems.logback.bytebuddy.ClassAdviceUtils.typeMatcher;
import static java.util.Collections.singleton;

public interface ClassAdviceConfig {

    ElementMatcher.Junction<? super MethodDescription> methods();

    ElementMatcher.Junction<? super TypeDescription> types();

    default ClassAdviceConfig join(ClassAdviceConfig other) {
        final ElementMatcher.Junction<? super MethodDescription> methodMatcher = methods().or(other.methods());
        final ElementMatcher.Junction<? super TypeDescription> typeMatcher = types().or(other.types());
        return new DefaultClassAdviceConfig(typeMatcher, methodMatcher);
    }

    public static ClassAdviceConfig create(String className, String methodName) {
        return new DefaultClassAdviceConfig(typeMatcher(singleton(className)), methodMatcher(singleton(methodName)));
    }

    public static ClassAdviceConfig create(String className, String... methodNames) {
        return new DefaultClassAdviceConfig(typeMatcher(singleton(className)), methodMatcher(Arrays.asList(methodNames)));
    }

    public static ClassAdviceConfig create(Collection<String> typeNames, Collection<String> methodNames) {
        return new DefaultClassAdviceConfig(typeMatcher(typeNames), methodMatcher(methodNames));
    }

}
