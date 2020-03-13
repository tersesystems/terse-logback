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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** A relative time converter that returns number of nanoseconds. */
public class RelativeTimeConverter extends ClassicConverter {
  @Override
  public String convert(ILoggingEvent event) {
    return Long.toString(System.nanoTime() - NanoTime.start);
  }
}
