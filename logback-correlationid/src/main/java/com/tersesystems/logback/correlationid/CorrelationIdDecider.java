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

package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.TurboFilterDecider;
import java.util.Optional;
import org.slf4j.Marker;

public class CorrelationIdDecider implements TurboFilterDecider {
  protected final CorrelationIdUtils utils;

  public CorrelationIdDecider(CorrelationIdUtils utils) {
    this.utils = utils;
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    Optional<String> maybeCorrelationId = utils.get(utils.getMDCPropertyMap(), marker);
    return maybeCorrelationId.isPresent() ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
  }
}
