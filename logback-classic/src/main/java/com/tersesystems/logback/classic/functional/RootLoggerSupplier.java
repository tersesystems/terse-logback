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
package com.tersesystems.logback.classic.functional;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.logback.classic.Utils;
import java.util.function.Supplier;

public class RootLoggerSupplier implements Supplier<Logger> {

  private final LoggerContext loggerContext;

  public RootLoggerSupplier(LoggerContext loggerContext) {
    this.loggerContext = loggerContext;
  }

  public Logger get() {
    return loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
  }

  public static RootLoggerSupplier create(LoggerContext loggerContext) {
    return new RootLoggerSupplier(loggerContext);
  }

  public static RootLoggerSupplier create() {
    return create(Utils.defaultContext());
  }
}
