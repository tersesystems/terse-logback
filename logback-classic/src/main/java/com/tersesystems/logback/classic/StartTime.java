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

/** This class pulls an Instant from a StartTimeSupplier. */
public final class StartTime {

  /**
   * Returns a start time from the event, then marker, then finally if not found will use the
   * event's timestamp as the marker.
   *
   * @param context the logging context
   * @param event the logging event
   * @return an instant representing the start time.
   */
  public static Instant from(Context context, ILoggingEvent event) {
    return fromOptional(context, event).orElse(Instant.ofEpochMilli(event.getTimeStamp()));
  }

  /**
   * Pulls a start time from the logging event, looking for the supplier on the event first, and
   * then looking for a StartTimeMarker.
   *
   * @param context the logback context
   * @param event the event
   * @return an optional start time, using both a container and marker as possible sources.
   */
  public static Optional<Instant> fromOptional(Context context, ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      ComponentContainer container = (ComponentContainer) event;
      if (container.hasComponent(StartTimeSupplier.class)) {
        return fromContainer(container);
      }
    }
    return fromMarker(context, event.getMarker());
  }

  /**
   * Looks for a StartTimeMarker in the marker and in all the children of the marker.
   *
   * @param context the logback context
   * @param m the logback marker
   * @return an optional start time.
   */
  public static Optional<Instant> fromMarker(Context context, Marker m) {
    if (m instanceof StartTimeSupplier) {
      StartTimeSupplier supplier = ((StartTimeSupplier) m);
      return Optional.of(supplier.getStartTime());
    }
    if (m != null && m.hasReferences()) {
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
    }
    return Optional.empty();
  }

  public static Optional<Instant> fromContainer(ComponentContainer container) {
    StartTimeSupplier supplier = container.getComponent(StartTimeSupplier.class);
    return Optional.ofNullable(supplier.getStartTime());
  }
}
