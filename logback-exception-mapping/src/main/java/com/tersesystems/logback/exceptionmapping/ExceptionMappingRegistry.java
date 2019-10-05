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
package com.tersesystems.logback.exceptionmapping;

import java.util.*;
import java.util.function.Function;

public interface ExceptionMappingRegistry {

  void register(Map<String, List<String>> mappers);

  void register(ClassLoader classLoader, Map<String, List<String>> mappers);

  void register(String className, String... methodNames);

  void register(ClassLoader classLoader, String className, String... methodNames);

  <E extends Throwable> void register(Class<E> exceptionClass, String... propertyNames);

  <E extends Throwable> void register(
      Class<E> exceptionClass, Function<E, List<ExceptionProperty>> f);

  void register(String className, Function<Throwable, List<ExceptionProperty>> f);

  void register(ExceptionMapping mapper);

  List<ExceptionProperty> apply(Throwable e);

  Iterator<ExceptionMapping> iterator();

  ExceptionMapping get(String name);

  boolean contains(ExceptionMapping exceptionMapping);

  boolean contains(String name);

  boolean remove(ExceptionMapping exceptionMapping);

  boolean remove(String name);
}
