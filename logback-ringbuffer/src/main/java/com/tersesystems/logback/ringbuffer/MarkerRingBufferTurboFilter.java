/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.ILoggingEventFactory;
import com.tersesystems.logback.classic.LoggingEventFactory;
import org.slf4j.Marker;

/**
 * A turbofilter that delegates recording and dumping of diagnostic logging information to external
 * markers. This allows for multiple ringbuffers and more flexible logging handling.
 */
public class MarkerRingBufferTurboFilter extends TurboFilter {
  private ILoggingEventFactory<ILoggingEvent> loggingEventFactory;

  public ILoggingEventFactory<ILoggingEvent> getLoggingEventFactory() {
    return loggingEventFactory;
  }

  public void setLoggingEventFactory(ILoggingEventFactory<ILoggingEvent> loggingEventFactory) {
    this.loggingEventFactory = loggingEventFactory;
  }

  @Override
  public void start() {
    if (this.loggingEventFactory == null) {
      this.loggingEventFactory = new LoggingEventFactory();
    }
    super.start();
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    if (isDumpTriggered(marker)) {
      dump(marker, logger);
    } else if (isRecordable(marker, logger, level)) {
      record(marker, logger, level, msg, params, t);
    }
    return FilterReply.NEUTRAL;
  }

  private boolean isRecordable(Marker marker, Logger logger, Level level) {
    // If the marker is going on a statement that's going to be logged in normal processing,
    // then don't allow it.
    if (level.isGreaterOrEqual(logger.getEffectiveLevel())) {
      return false;
    }
    return marker instanceof RingBufferMarkerFactory.RecordMarker;
  }

  private boolean isDumpTriggered(Marker marker) {
    return marker instanceof RingBufferMarkerFactory.TriggerMarker;
  }

  protected void dump(Marker marker, Logger logger) {
    RingBuffer<ILoggingEvent> ringBuffer = getRingBuffer(marker);
    for (ILoggingEvent iLoggingEvent : ringBuffer) {
      logger.callAppenders(iLoggingEvent);
    }
    ringBuffer.clear();
  }

  protected void record(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    ILoggingEvent le = loggingEventFactory.create(marker, logger, level, msg, params, t);
    RingBuffer<ILoggingEvent> ringBuffer = getRingBuffer(marker);
    ringBuffer.append(le);
  }

  @SuppressWarnings("unchecked")
  protected RingBuffer<ILoggingEvent> getRingBuffer(Marker marker) {
    return ((RingBufferAware<ILoggingEvent>) marker).getRingBuffer();
  }
}
