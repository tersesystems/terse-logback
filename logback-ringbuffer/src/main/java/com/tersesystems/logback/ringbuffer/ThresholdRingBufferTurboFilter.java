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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Marker;

/** Dumps logging events if an event with a level meeting the threshold level is seen. */
public class ThresholdRingBufferTurboFilter extends TurboFilter
    implements RingBufferAware<ILoggingEvent> {

  private int capacity = 100;
  private RingBuffer<ILoggingEvent> ringBuffer;
  private ILoggingEventFactory<ILoggingEvent> loggingEventFactory;
  private List<String> loggerList = new ArrayList<>();
  private String loggerContextName = "loggerList";

  private Level triggerLevel = Level.ERROR;
  private Level recordLevel = Level.DEBUG;

  public void setRecordLevel(String recordLevel) {
    this.recordLevel = Level.toLevel(recordLevel);
  }

  public void setTriggerLevel(String triggerLevel) {
    this.triggerLevel = Level.toLevel(triggerLevel);
  }

  public void addLogger(String logger) {
    this.loggerList.add(logger);
  }

  public void setLoggerContextName(String loggerContextName) {
    this.loggerContextName = loggerContextName;
  }

  @Override
  public void start() {
    if (recordLevel.isGreaterOrEqual(triggerLevel)) {
      addError("Threshold is lower or equal to level!");
    }
    if (loggingEventFactory == null) {
      this.loggingEventFactory = new LoggingEventFactory();
    }
    if (this.loggerList.isEmpty()) {
      // Can't seem to set a list of strings directly using setProperty, so let's pull it from
      // context.
      if (this.loggerContextName != null) {
        Collection<String> loggers =
            (Collection<String>) getContext().getObject(this.loggerContextName);
        this.loggerList.addAll(loggers);
      } else {
        addWarn("No logger name was specified");
      }
    }
    ringBuffer = new RingBuffer<>(capacity);

    super.start();
  }

  public boolean isDumpTriggered(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    return level.isGreaterOrEqual(triggerLevel);
  }

  public boolean isRecordable(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    // Can't use isEnabledFor here because that itself goes through the turbofilters.
    if (level.isGreaterOrEqual(logger.getEffectiveLevel())) return false;
    if (level.isGreaterOrEqual(recordLevel)) return isSelectedLogger(logger);
    return false;
  }

  public void setLoggingEventFactory(ILoggingEventFactory<ILoggingEvent> loggingEventFactory) {
    this.loggingEventFactory = loggingEventFactory;
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    if (isDumpTriggered(marker, logger, level, msg, params, t)) {
      dump(logger);
    } else if (isRecordable(marker, logger, level, msg, params, t)) {
      record(marker, logger, level, msg, params, t);
    }
    return FilterReply.NEUTRAL;
  }

  protected void dump(Logger logger) {
    for (ILoggingEvent iLoggingEvent : ringBuffer) {
      logger.callAppenders(iLoggingEvent);
    }
    ringBuffer.clear();
  }

  protected void record(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    ILoggingEvent le = loggingEventFactory.create(marker, logger, level, msg, params, t);
    ringBuffer.append(le);
  }

  protected boolean isSelectedLogger(Logger logger) {
    String name = logger.getName();
    boolean result = loggerList.stream().anyMatch(name::startsWith);
    return result;
  }

  @Override
  public RingBuffer<ILoggingEvent> getRingBuffer() {
    return ringBuffer;
  }
}
