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

import java.util.Arrays;

public interface ExceptionProperty {

  public static ExceptionProperty create(String name, String value) {
    return new KeyValueExceptionProperty(name, value);
  }

  public static ExceptionProperty create(String name, Object value) {
    return new KeyValueExceptionProperty(name, toString(value));
  }

  static String toString(Object value) {
    if (value instanceof boolean[]) return Arrays.toString((boolean[]) value);
    if (value instanceof byte[]) return Arrays.toString((byte[]) value);
    if (value instanceof short[]) return Arrays.toString((short[]) value);
    if (value instanceof char[]) return Arrays.toString((char[]) value);
    if (value instanceof int[]) return Arrays.toString((int[]) value);
    if (value instanceof long[]) return Arrays.toString((long[]) value);
    if (value instanceof float[]) return Arrays.toString((float[]) value);
    if (value instanceof double[]) return Arrays.toString((double[]) value);
    if (value instanceof Object[]) return Arrays.deepToString((Object[]) value);
    if (value == null) {
      return "null";
    }
    return value.toString();
  }
}
