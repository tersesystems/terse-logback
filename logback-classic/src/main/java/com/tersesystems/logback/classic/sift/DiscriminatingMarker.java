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
package com.tersesystems.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.classic.TerseBasicMarker;
import java.util.function.Function;

public class DiscriminatingMarker extends TerseBasicMarker implements DiscriminatingValue {

  private static final String TS_DISCRIMINATING_MARKER = "TS_DESCRIMINATING_MARKER";
  private final Function<ILoggingEvent, String> discriminatingFunction;

  public DiscriminatingMarker(Function<ILoggingEvent, String> discriminatingFunction) {
    super(TS_DISCRIMINATING_MARKER);
    this.discriminatingFunction = discriminatingFunction;
  }

  @Override
  public String getDiscriminatingValue(ILoggingEvent loggingEvent) {
    return discriminatingFunction.apply(loggingEvent);
  }
}
