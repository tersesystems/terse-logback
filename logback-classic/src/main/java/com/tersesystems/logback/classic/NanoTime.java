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
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Marker;

public final class NanoTime {
  public static final long start = System.nanoTime();

  public static Optional<Long> fromOptional(Context context, ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      ComponentContainer container = (ComponentContainer) event;
      if (container.hasComponent(NanoTimeSupplier.class)) {
        return fromContainer(container);
      }
    }

    return fromMarker(context, event.getMarker());
  }

  static Optional<Long> fromMarker(Context context, Marker m) {
    if (m instanceof NanoTimeSupplier) {
      NanoTimeSupplier supplier = ((NanoTimeSupplier) m);
      return Optional.of(supplier.getNanoTime());
    }
    if (m != null && m.hasReferences()) {
      for (Iterator<Marker> iter = m.iterator(); iter.hasNext(); ) {
        Marker child = iter.next();
        if (child instanceof ContextAware) {
          ((ContextAware) child).setContext(context);
        }
        if (child instanceof NanoTimeSupplier) {
          NanoTimeSupplier supplier = ((NanoTimeSupplier) child);
          return Optional.of(supplier.getNanoTime());
        }
        if (child.hasReferences()) {
          return fromMarker(context, child);
        }
      }
    }
    return Optional.empty();
  }

  public static Optional<Long> fromContainer(ComponentContainer container) {
    long nanoTime = container.getComponent(NanoTimeSupplier.class).getNanoTime();
    return Optional.of(nanoTime);
  }
}
