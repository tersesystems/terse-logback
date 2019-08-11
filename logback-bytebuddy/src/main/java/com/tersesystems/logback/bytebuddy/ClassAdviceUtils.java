package com.tersesystems.logback.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.StringMatcher;

import java.util.Collection;
import java.util.List;

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

    public static AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return new AgentBuilder.Listener.Filtering(
                ClassAdviceUtils.stringMatcher(classNames),
                AgentBuilder.Listener.StreamWriting.toSystemOut());
    }

}
