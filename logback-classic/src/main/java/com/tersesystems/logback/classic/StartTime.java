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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import com.tersesystems.logback.core.ComponentContainer;
import com.tersesystems.logback.core.StreamUtils;
import java.time.Instant;
import java.util.Optional;

public class StartTime {

  public static Optional<Instant> fromOptional(Context context, ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      return fromContainer((ComponentContainer) event);
    }

    return StreamUtils.fromMarker(context, event.getMarker())
        .filter(marker -> marker instanceof StartTimeSupplier)
        .map(marker -> (StartTimeSupplier) marker)
        .map(StartTimeSupplier::getStartTime)
        .findFirst();
  }

  public static Optional<Instant> fromContainer(ComponentContainer container) {
    StartTimeSupplier supplier = container.getComponent(StartTimeSupplier.class);
    return Optional.ofNullable(supplier.getStartTime());
  }

  public static Instant from(Context context, ILoggingEvent eventObject) {
    return fromOptional(context, eventObject)
        .orElse(Instant.ofEpochMilli(eventObject.getTimeStamp()));
  }
}
