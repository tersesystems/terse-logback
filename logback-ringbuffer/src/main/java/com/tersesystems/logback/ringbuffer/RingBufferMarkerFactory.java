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
package com.tersesystems.logback.ringbuffer;

import java.util.function.Supplier;
import org.slf4j.Marker;

/**
 * A marker factory that contains a ringbuffer and two inner classes, RecordMarker and DumpMarker.
 *
 * <p>Using a ring buffer marker factory means that you can build up a thread of messages and dump
 * the ring buffer at a later point, for example:
 *
 * <pre>{@code
 * RingBuffer ringBuffer = getRingBuffer();
 * RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(ringBuffer);
 * Marker recordMarker = markerFactory.createRecordMarker();
 * Marker dumpMarker = markerFactory.createTriggerMarker();
 *
 * Logger logger = loggerFactory.getLogger("com.example.Test");
 * logger.debug(recordMarker, "debug one");
 * logger.debug(recordMarker, "debug two");
 * logger.debug(recordMarker, "debug three");
 * logger.debug(recordMarker, "debug four");
 * logger.error(dumpMarker, "Dump all the messages");
 * }</pre>
 */
public class RingBufferMarkerFactory {
  private final RingBufferContextAware ringBuffer;

  public RingBufferMarkerFactory(RingBufferContextAware ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public Marker createTriggerMarker() {
    return new TriggerMarker(() -> ringBuffer);
  }

  public Marker createRecordMarker() {
    return new RecordMarker(() -> ringBuffer);
  }

  public class RecordMarker extends AbstractRingBufferMarker {
    static final String TS_RECORD_MARKER = "TS_RECORD_MARKER";

    RecordMarker(Supplier<RingBufferContextAware> supplier) {
      super(TS_RECORD_MARKER, supplier);
    }
  }

  public class TriggerMarker extends AbstractRingBufferMarker {
    static final String TS_RECORD_MARKER = "TS_DUMP_MARKER";

    TriggerMarker(Supplier<RingBufferContextAware> supplier) {
      super(TS_RECORD_MARKER, supplier);
    }
  }
}
