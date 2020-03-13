/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.exceptionmapping;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanExceptionMapping implements ExceptionMapping {
  private final List<String> methodNames;
  private final Consumer<Exception> reporter;
  private final String name;

  public BeanExceptionMapping(
      String name, List<String> propertyNames, Consumer<Exception> reporter) {
    this.name = name;
    this.methodNames = propertyNames;
    this.reporter = reporter;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<ExceptionProperty> apply(Throwable e) {
    return methodNames.stream()
        .flatMap(methodName -> findMethod(e, methodName))
        .collect(Collectors.toList());
  }

  protected Stream<ExceptionProperty> findMethod(Throwable e, String methodName) {
    return Arrays.stream(e.getClass().getMethods())
        .filter(
            method ->
                methodName.equals(BeanUtil.getPropertyName(method)) && BeanUtil.isGetter(method))
        .map(
            method -> {
              try {
                Object invokeResult = method.invoke(e, (Object[]) null);
                return Optional.of(ExceptionProperty.create(methodName, invokeResult));
              } catch (IllegalAccessException | InvocationTargetException ex) {
                reporter.accept(ex);
              }
              return Optional.<ExceptionProperty>empty();
            })
        .filter(Optional::isPresent)
        .map(Optional::get);
  }
}
