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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

public class TurboMarkerDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
  @Override
  public String getDiscriminatingValue(ILoggingEvent event) {
    // Use this to create user specific circular buffers
    event.getMarker();
    return null;
  }

  @Override
  public String getKey() {
    return null;
  }
}
