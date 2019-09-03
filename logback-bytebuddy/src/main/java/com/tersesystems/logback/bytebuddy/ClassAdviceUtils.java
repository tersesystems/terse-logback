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
import net.bytebuddy.matcher.StringMatcher;

import java.util.Collection;

/**
 * Utils for LoggingAdviceConfig.
 */
public final class ClassAdviceUtils {

    public static ElementMatcher.Junction<? super MethodDescription> methodMatcher(Collection<String> methodNames) {
        return methodNames.stream().map(ElementMatchers::named).reduce(ElementMatchers.none(), ElementMatcher.Junction::or);
    }

    public static ElementMatcher.Junction<? super TypeDescription> typeMatcher(Collection<String> typeNames) {
        return typeNames.stream().map(ElementMatchers::nameContains).reduce(ElementMatchers.none(), ElementMatcher.Junction::or);
    }

    public static ElementMatcher.Junction<? super String> stringMatcher(Collection<String> typeNames) {
        boolean seen = false;
        ElementMatcher.Junction<? super String> acc = ElementMatchers.none();
        for (String typeName : typeNames) {
            StringMatcher stringMatcher = new StringMatcher(typeName, StringMatcher.Mode.EQUALS_FULLY);
            if (!seen) {
                seen = true;
                acc = stringMatcher;
            } else {
                acc = acc.or(stringMatcher);
            }
        }
        return acc;
    }
}
