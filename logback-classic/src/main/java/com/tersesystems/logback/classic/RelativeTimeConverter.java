package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** A relative time converter that returns number of nanoseconds. */
public class RelativeTimeConverter extends ClassicConverter {
  @Override
  public String convert(ILoggingEvent event) {
    return Long.toString(System.nanoTime() - NanoTime.start);
  }
}
