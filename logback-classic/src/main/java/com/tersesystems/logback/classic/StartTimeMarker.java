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
package com.tersesystems.logback.classic;

import java.time.Instant;
import java.util.Objects;

public class StartTimeMarker extends TerseBasicMarker implements StartTimeSupplier {
  private static final String TS_STARTTIME_MARKER = "TS_STARTTIME_MARKER";
  private final Instant startTime;

  public StartTimeMarker(Instant start) {
    super(TS_STARTTIME_MARKER);
    this.startTime = Objects.requireNonNull(start);
  }

  @Override
  public Instant getStartTime() {
    return startTime;
  }

  @Override
  public String toString() {
    return "StartTimeMarker{" + "startTime=" + startTime + '}';
  }
}
