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
import ch.qos.logback.core.spi.ContextAware;
import com.tersesystems.logback.core.ComponentContainer;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Marker;

public class StartTime {

  public static Optional<Instant> fromOptional(Context context, ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      ComponentContainer container = (ComponentContainer) event;
      if (container.hasComponent(StartTimeSupplier.class)) {
        return fromContainer(container);
      }
    }
    return fromMarker(context, event.getMarker());
  }

  static Optional<Instant> fromMarker(Context context, Marker m) {
    if (m instanceof StartTimeSupplier) {
      StartTimeSupplier supplier = ((StartTimeSupplier) m);
      return Optional.of(supplier.getStartTime());
    }
    for (Iterator<Marker> iter = m.iterator(); iter.hasNext(); ) {
      Marker child = iter.next();
      if (child instanceof ContextAware) {
        ((ContextAware) child).setContext(context);
      }
      if (child instanceof StartTimeSupplier) {
        StartTimeSupplier supplier = ((StartTimeSupplier) child);
        return Optional.of(supplier.getStartTime());
      }
      if (child.hasReferences()) {
        return fromMarker(context, child);
      }
    }
    return Optional.empty();
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
