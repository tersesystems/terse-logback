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
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static AdviceConfig create(ClassLoader classLoader, String className, List<String> methodNames) throws Exception {
        TypeDescription aClass = createTypeDescription(classLoader, className);

        return new AdviceConfig(singletonList(className), is(aClass), methodNames.stream()
                .map(m -> named(m).and(isDeclaredBy(aClass)).and(not(isNative().or(isConstructor()))))
                .reduce(none(), ElementMatcher.Junction::or));
    }

    public static AdviceConfig create(ClassLoader classLoader, String className) throws Exception {
        TypeDescription aClass = createTypeDescription(classLoader, className);

        // Get a list of all non-native methods from the class, with no constructor.
        MethodList<MethodDescription.InDefinedShape> methods = aClass.getDeclaredMethods()
                .filter(not(isNative().or(isConstructor())));
        return new AdviceConfig(singletonList(className), is(aClass), anyOf(methods));
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

    // Push a class definition into any classloader (useful for on the fly created types)
    //private static void injectClass(ClassLoader classLoader, String className, byte[] classBytes) throws IOException {
    //    //byte[] classBytes = ClassFileLocator.ForJarFile.ofClassPath().locate(className).resolve();
    //    Map<String, byte[]> byteMap = Collections.singletonMap(className, classBytes);
    //    ClassInjector classInjector = new ClassInjector.UsingReflection(classLoader);
    //    classInjector.injectRaw(byteMap);
    //}

    private static TypeDescription createTypeDescription(ClassLoader classLoader, String className) throws Exception {
        return new TypeDescription.ForLoadedType(classLoader.loadClass(className));
    }
}
