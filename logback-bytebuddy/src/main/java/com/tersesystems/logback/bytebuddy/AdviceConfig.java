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

import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.*;

import java.util.*;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class AdviceConfig {

  private final ClassLoader classLoader;

  private final Collection<TraceConfig> traceConfigCollection;
  private final String serviceName;

  private TraceConfig traceConfig;

  public AdviceConfig(ClassLoader classLoader, String serviceName) {
    this.classLoader = classLoader;
    this.serviceName = serviceName;
    traceConfigCollection = new HashSet<>();
  }

  public String getServiceName() {
    return serviceName;
  }

  public void addTrace(String className, List<String> methodNames) throws Exception {
    TraceConfig traceConfig = TraceConfig.create(classLoader, className, methodNames);
    traceConfigCollection.add(traceConfig);
  }

  public void addTrace(String className) throws Exception {
    TraceConfig traceConfig = TraceConfig.create(classLoader, className);
    traceConfigCollection.add(traceConfig);
  }

  public List<String> classNames() {
    return getTraceConfig().classNames;
  }

  public ElementMatcher<? super MethodDescription> methods() {
    return getTraceConfig().methods();
  }

  public ElementMatcher<? super TypeDescription> types() {
    return getTraceConfig().types();
  }

  private TraceConfig getTraceConfig() {
    if (traceConfig == null) {
      traceConfig = traceConfigCollection.stream().reduce(TraceConfig::join).get();
    }
    return traceConfig;
  }

  static final class TraceConfig {

    private final List<String> classNames;
    private final ElementMatcher.Junction<? super TypeDescription> typeMatcher;
    private final ElementMatcher.Junction<? super MethodDescription> methodMatcher;

    private TraceConfig(
        List<String> classNames,
        ElementMatcher.Junction<? super TypeDescription> typeMatcher,
        ElementMatcher.Junction<? super MethodDescription> methodMatcher) {
      this.typeMatcher = Objects.requireNonNull(typeMatcher);
      this.methodMatcher = Objects.requireNonNull(methodMatcher);
      this.classNames = classNames;
    }

    static TraceConfig create(ClassLoader classLoader, String className, List<String> methodNames)
        throws Exception {
      TypeDescription aClass = createTypeDescription(classLoader, className);

      return new TraceConfig(
          singletonList(className),
          is(aClass),
          methodNames.stream()
              .map(m -> named(m).and(isDeclaredBy(aClass)).and(not(isNative().or(isConstructor()))))
              .reduce(none(), ElementMatcher.Junction::or));
    }

    static TraceConfig create(ClassLoader classLoader, String className) throws Exception {
      TypeDescription aClass = createTypeDescription(classLoader, className);

      // Get a list of all non-native methods from the class, with no constructor.
      MethodList<MethodDescription.InDefinedShape> methods =
          aClass.getDeclaredMethods().filter(not(isNative().or(isConstructor())));
      return new TraceConfig(singletonList(className), is(aClass), anyOf(methods));
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

    public TraceConfig join(TraceConfig other) {
      final ElementMatcher.Junction<? super MethodDescription> methodMatcher =
          methods().or(other.methods());
      final ElementMatcher.Junction<? super TypeDescription> typeMatcher =
          types().or(other.types());
      List<String> classNames = new ArrayList<>(classNames());
      classNames.addAll(other.classNames());
      return new TraceConfig(classNames, typeMatcher, methodMatcher);
    }

    private static TypeDescription createTypeDescription(ClassLoader classLoader, String className)
        throws Exception {
      return new TypeDescription.ForLoadedType(classLoader.loadClass(className));
    }
  }

  @Override
  public String toString() {
    return "AdviceConfig{service-name = "
        + getServiceName()
        + ", methods="
        + methods()
        + ", types='"
        + types()
        + '\''
        + '}';
  }
}
