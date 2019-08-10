package com.tersesystems.logback.bytebuddy;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.matcher.ElementMatchers.named;

public interface ClassAdviceConfig {

    public ElementMatcher.Junction<? super MethodDescription> methods();

    public ElementMatcher.Junction<? super TypeDescription> types();

    static ClassAdviceConfig create(String className, String methodName) {
        return new DefaultClassAdviceConfig(ElementMatchers.nameContains(className), named(methodName));
    }

    default ClassAdviceConfig join(ClassAdviceConfig other) {
        final ElementMatcher.Junction<MethodDescription> methodMatcher = methods().and(other.methods());
        final ElementMatcher.Junction<? super TypeDescription> typeMatcher = types().and(other.types());

        return new ClassAdviceConfig() {
            @Override
            public ElementMatcher.Junction<? super MethodDescription> methods() {
                return methodMatcher;
            }

            @Override
            public ElementMatcher.Junction<? super TypeDescription> types() {
                return typeMatcher;
            }
        };

    }
}
