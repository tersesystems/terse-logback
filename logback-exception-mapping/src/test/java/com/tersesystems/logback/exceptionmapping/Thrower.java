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

import java.sql.BatchUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thrower {
  private static final Logger logger = LoggerFactory.getLogger(Thrower.class);

  public static void main(String[] progArgs) {
    try {
      doSomethingExceptional();
    } catch (RuntimeException e) {
      logger.error("domain specific message", e);
    }
  }

  static void doSomethingExceptional() {
    Throwable cause = new BatchUpdateException();
    throw new MyCustomException(
        "This is my message",
        "one is one",
        "two is more than one",
        "three is more than two and one",
        cause);
  }
}
