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
package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import java.util.function.BiFunction;
import org.slf4j.Marker;

@FunctionalInterface
public interface MarkerContextDecider<C>
    extends BiFunction<ContextAwareTurboMarker<C>, C, FilterReply>,
        ContextAwareTurboFilterDecider<C> {
  @Override
  default FilterReply decide(
      ContextAwareTurboMarker<C> marker,
      C context,
      Marker rootMarker,
      Logger logger,
      Level level,
      String format,
      Object[] params,
      Throwable t) {
    return apply(marker, context);
  }
}
