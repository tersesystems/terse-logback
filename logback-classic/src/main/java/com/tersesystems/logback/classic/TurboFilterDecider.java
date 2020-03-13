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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

/**
 * An interface that decides what sort of filter reply there is.
 *
 * <p>Logback doesn't provide an interface for this out of the box for all turbofilters, so we have
 * to add one in by hand when we want decisions without the whole turbo filter.
 *
 * <p>This comes in handy for turbomarkers and tap filters.
 */
public interface TurboFilterDecider {
  FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t);
}
