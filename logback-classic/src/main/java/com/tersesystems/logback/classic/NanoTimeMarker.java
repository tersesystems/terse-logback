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

public class NanoTimeMarker extends TerseBasicMarker implements NanoTimeSupplier {
  private static final String NANOTIME_MARKER_NAME = "TS_NANOTIME_MARKER";
  private final long nanoTime;

  public NanoTimeMarker() {
    super(NANOTIME_MARKER_NAME);
    this.nanoTime = System.nanoTime() - NanoTime.start;
  }

  public long getNanoTime() {
    return nanoTime;
  }

  public static NanoTimeMarker create() {
    return new NanoTimeMarker();
  }

  @Override
  public String toString() {
    return "NanoTimeMarker{" + "nanoTime=" + nanoTime + '}';
  }
}
