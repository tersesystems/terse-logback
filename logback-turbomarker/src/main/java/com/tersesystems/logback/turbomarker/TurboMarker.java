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
package com.tersesystems.logback.turbomarker;

import com.tersesystems.logback.classic.TerseBasicMarker;
import com.tersesystems.logback.classic.TurboFilterDecider;

/**
 * This class is a marker that can test to see whether an event should be allowed through a turbo
 * filter.
 */
public abstract class TurboMarker extends TerseBasicMarker implements TurboFilterDecider {
  public TurboMarker(String name) {
    super(name);
  }
}
