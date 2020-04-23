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
package com.tersesystems.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.sift.DefaultDiscriminator;
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Marker;

/**
 * A discriminator that looks for a marker containing discriminating logic.
 *
 * @param <LoggingEventT> the logging event type
 */
public class MarkerBasedDiscriminator<LoggingEventT extends ILoggingEvent>
    extends AbstractDiscriminator<LoggingEventT> {

  private String key = "key";
  private String defaultValue = DefaultDiscriminator.DEFAULT;

  @Override
  public String getDiscriminatingValue(ILoggingEvent loggingEvent) {
    Optional<DiscriminatingValue> optMarker = getDiscriminatorMarker(loggingEvent);
    return optMarker.map(m -> m.getDiscriminatingValue(loggingEvent)).orElse(getDefaultValue());
  }

  public Optional<DiscriminatingValue> getDiscriminatorMarker(ILoggingEvent loggingEvent) {
    return fromMarker(loggingEvent.getMarker());
  }

  static Optional<DiscriminatingValue> fromMarker(Marker m) {
    if (m instanceof DiscriminatingValue) {
      DiscriminatingValue value = ((DiscriminatingValue) m);
      return Optional.of(value);
    }
    for (Iterator<Marker> iter = m.iterator(); iter.hasNext(); ) {
      Marker child = iter.next();
      if (child instanceof DiscriminatingValue) {
        DiscriminatingValue value = ((DiscriminatingValue) child);
        return Optional.of(value);
      }
      if (child.hasReferences()) {
        return fromMarker(child);
      }
    }
    return Optional.empty();
  }

  @Override
  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
