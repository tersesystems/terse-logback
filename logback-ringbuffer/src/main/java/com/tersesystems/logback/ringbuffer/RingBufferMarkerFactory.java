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

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.function.Supplier;
import org.slf4j.Marker;

/**
 * A marker factory that contains a ringbuffer and two inner classes, RecordMarker and DumpMarker.
 *
 * <p>A logging statement that has record marker added will be appended to the
 */
public class RingBufferMarkerFactory {
  private final RingBuffer<ILoggingEvent> ringBuffer;

  public RingBufferMarkerFactory(int capacity) {
    this.ringBuffer = new RingBuffer<>(capacity);
  }

  public Marker createTriggerMarker() {
    return new TriggerMarker(() -> ringBuffer);
  }

  public Marker createRecordMarker() {
    return new RecordMarker(() -> ringBuffer);
  }

  public static class RecordMarker extends AbstractRingBufferMarker<ILoggingEvent> {
    static final String TS_RECORD_MARKER = "TS_RECORD_MARKER";

    RecordMarker(Supplier<RingBuffer<ILoggingEvent>> supplier) {
      super(TS_RECORD_MARKER, supplier);
    }
  }

  public static class TriggerMarker extends AbstractRingBufferMarker<ILoggingEvent> {
    static final String TS_RECORD_MARKER = "TS_DUMP_MARKER";

    TriggerMarker(Supplier<RingBuffer<ILoggingEvent>> supplier) {
      super(TS_RECORD_MARKER, supplier);
    }
  }
}
