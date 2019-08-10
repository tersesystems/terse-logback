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
