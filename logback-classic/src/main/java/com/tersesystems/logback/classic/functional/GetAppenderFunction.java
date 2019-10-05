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
package com.tersesystems.logback.classic.functional;

import static java.util.Objects.requireNonNull;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.tersesystems.logback.classic.Utils;
import java.util.Optional;
import java.util.function.Function;

public class GetAppenderFunction<A extends Appender<ILoggingEvent>>
    implements Function<String, Optional<A>> {

  private final Logger rootLogger;

  public GetAppenderFunction(Logger rootLogger) {
    this.rootLogger = rootLogger;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<A> apply(String appenderName) {
    Appender<ILoggingEvent> appender = rootLogger.getAppender(requireNonNull(appenderName));
    try {
      return Optional.ofNullable((A) appender);
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }

  public static <AT extends Appender<ILoggingEvent>> GetAppenderFunction<AT> create() {
    return create(Utils.defaultContext());
  }

  public static <AT extends Appender<ILoggingEvent>> GetAppenderFunction<AT> create(
      LoggerContext context) {
    Logger logger = RootLoggerSupplier.create(context).get();
    return new GetAppenderFunction<>(logger);
  }
}
