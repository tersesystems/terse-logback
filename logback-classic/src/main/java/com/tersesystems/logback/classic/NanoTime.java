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
import java.util.Optional;

public final class NanoTime {
  public static final long start = System.nanoTime();

  public static Optional<Long> fromOptional(Context context, ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      ComponentContainer container = (ComponentContainer) event;
      if (container.hasComponent(NanoTimeSupplier.class)) {
        return fromContainer(container);
      }
    }

    return StreamUtils.fromMarker(context, event.getMarker())
        .filter(marker -> marker instanceof NanoTimeSupplier)
        .map(marker -> (NanoTimeSupplier) marker)
        .map(NanoTimeSupplier::getNanoTime)
        .findFirst();
  }

  public static Optional<Long> fromContainer(ComponentContainer container) {
    long nanoTime = container.getComponent(NanoTimeSupplier.class).getNanoTime();
    return Optional.of(nanoTime);
  }
}
