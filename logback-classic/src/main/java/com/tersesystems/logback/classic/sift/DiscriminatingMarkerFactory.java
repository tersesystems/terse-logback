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
import java.util.function.Function;

public class DiscriminatingMarkerFactory {

  private final Function<ILoggingEvent, String> discriminatingFunction;

  public DiscriminatingMarkerFactory(Function<ILoggingEvent, String> discriminatingFunction) {
    this.discriminatingFunction = discriminatingFunction;
  }

  public static DiscriminatingMarkerFactory create(
      Function<ILoggingEvent, String> discriminatingFunction) {
    return new DiscriminatingMarkerFactory(discriminatingFunction);
  }

  public DiscriminatingMarker createMarker() {
    return new DiscriminatingMarker(discriminatingFunction);
  }
}
