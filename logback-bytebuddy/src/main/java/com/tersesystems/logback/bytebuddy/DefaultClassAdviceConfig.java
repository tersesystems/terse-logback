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

import java.util.Objects;

public class DefaultClassAdviceConfig implements ClassAdviceConfig {
        private final ElementMatcher.Junction<? super TypeDescription> typeMatcher;
        private final ElementMatcher.Junction<? super MethodDescription> methodMatcher;

        public DefaultClassAdviceConfig(ElementMatcher.Junction<? super TypeDescription> typeMatcher,
                      ElementMatcher.Junction<? super MethodDescription> methodMatcher) {
            this.typeMatcher = Objects.requireNonNull(typeMatcher);
            this.methodMatcher = Objects.requireNonNull(methodMatcher);
        }

        public ElementMatcher.Junction<? super MethodDescription> methods() {
            return methodMatcher;
        }

        public ElementMatcher.Junction<? super TypeDescription> types() {
            return typeMatcher;
        }
    }
