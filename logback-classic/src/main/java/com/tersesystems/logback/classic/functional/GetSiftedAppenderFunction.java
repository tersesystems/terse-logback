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

import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.sift.AppenderTracker;
import java.util.Optional;
import java.util.function.Function;

public class GetSiftedAppenderFunction<A> implements Function<SiftingAppender, Optional<A>> {

  private final String key;

  public GetSiftedAppenderFunction(String key) {
    this.key = key;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<A> apply(SiftingAppender siftingAppender) {
    AppenderTracker<ILoggingEvent> appenderTracker = siftingAppender.getAppenderTracker();
    try {
      return Optional.ofNullable((A) appenderTracker.find(key));
    } catch (ClassCastException e) {
      return Optional.empty();
    }
  }

  public static <AT extends Appender<ILoggingEvent>> GetSiftedAppenderFunction<AT> create(
      String key) {
    return new GetSiftedAppenderFunction<AT>(key);
  }
}
