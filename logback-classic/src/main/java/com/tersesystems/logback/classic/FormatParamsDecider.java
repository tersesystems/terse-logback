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
package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import java.util.function.BiFunction;
import org.slf4j.Marker;

@FunctionalInterface
public interface FormatParamsDecider
    extends BiFunction<String, Object[], FilterReply>, TurboFilterDecider {
  @Override
  default FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    return apply(format, params);
  }
}
