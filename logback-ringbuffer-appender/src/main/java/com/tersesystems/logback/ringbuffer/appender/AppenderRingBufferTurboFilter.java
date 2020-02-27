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
package com.tersesystems.logback.ringbuffer.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.ILoggingEventFactory;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import com.tersesystems.logback.ringbuffer.BufferedLoggingEvent;
import com.tersesystems.logback.ringbuffer.EncodingRingBufferAppender;
import com.tersesystems.logback.ringbuffer.RingBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.logstash.logback.encoder.CompositeJsonEncoder;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

/**
 * This turbo filter will pull diagnostic events from a cyclic buffer and add them as a logstash
 * marker.
 */
public class AppenderRingBufferTurboFilter extends TurboFilter
    implements DefaultAppenderAttachable<ILoggingEvent> {

  private Level triggerLevel = Level.ERROR;
  private Level recordLevel = Level.DEBUG;
  private CompositeJsonEncoder<ILoggingEvent> encoder;
  private String fieldName;
  private ILoggingEventFactory<ILoggingEvent> loggingEventFactory;

  public CompositeJsonEncoder<ILoggingEvent> getEncoder() {
    return encoder;
  }

  public void setEncoder(CompositeJsonEncoder<ILoggingEvent> encoder) {
    this.encoder = encoder;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public void setRecordLevel(String recordLevel) {
    this.recordLevel = Level.toLevel(recordLevel);
  }

  public void setTriggerLevel(String triggerLevel) {
    this.triggerLevel = Level.toLevel(triggerLevel);
  }

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
    if (recordLevel.isGreaterOrEqual(triggerLevel)) {
      addError("Threshold is lower or equal to level!");
    }
    if (encoder == null) {
      this.encoder = new LogstashEncoder();
      this.encoder.setContext(getContext());
      this.encoder.start();
    }
    if (fieldName == null) {
      fieldName = "diagnosticEvents";
    }
    super.start();
  }

  private final AppenderAttachableImpl<ILoggingEvent> aae = new AppenderAttachableImpl<>();

  private EncodingRingBufferAppender findRingBufferAppender() {
    Iterator<Appender<ILoggingEvent>> appenderIterator = aae.iteratorForAppenders();
    while (appenderIterator.hasNext()) {
      Appender<ILoggingEvent> next = appenderIterator.next();
      if (next instanceof EncodingRingBufferAppender) {
        return (EncodingRingBufferAppender) next;
      }
    }
    return null;
  }

  public boolean isDumpTriggered(
      Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
    return level.isGreaterOrEqual(triggerLevel);
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (isDumpTriggered(marker, logger, level, format, params, t)) {
      EncodingRingBufferAppender ringBufferAppender = findRingBufferAppender();
      if (ringBufferAppender == null) {
        addError("No ringBufferAppender appender found!");
        return FilterReply.NEUTRAL;
      }

      RingBuffer ringBuffer = ringBufferAppender.getRingBuffer();
      LogstashMarker diagnosticEventMarker =
          createBacktraceMarker(ringBuffer, ringBufferAppender::reset);
      Marker joinedMarker = joinMarkers(diagnosticEventMarker, marker);
      ILoggingEventFactory<ILoggingEvent> loggingEventFactory = getLoggingEventFactory();
      ILoggingEvent event =
          loggingEventFactory.create(joinedMarker, logger, level, format, params, t);
      logger.callAppenders(event);
      return FilterReply.DENY;
    }

    return FilterReply.NEUTRAL;
  }

  public LogstashMarker createBacktraceMarker(RingBuffer ringBuffer, Runnable afterHook) {
    return Markers.defer(
        () -> {
          try {
            Stream<BufferedLoggingEvent> stream = ringBuffer.stream();
            String eventJson = stream.map(this::encodeEventToJson).collect(Collectors.joining(","));
            return Markers.appendRaw(getFieldName(), "[" + eventJson + "]");
          } finally {
            afterHook.run();
          }
        });
  }

  private Marker joinMarkers(LogstashMarker diagnosticEventMarker, Marker marker) {
    if (marker == null) {
      return diagnosticEventMarker;
    }
    return Markers.aggregate(diagnosticEventMarker, marker);
  }

  protected String encodeEventToJson(BufferedLoggingEvent event) {
    return new String(event.getEncodedData(), StandardCharsets.UTF_8);
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return aae;
  }
}
