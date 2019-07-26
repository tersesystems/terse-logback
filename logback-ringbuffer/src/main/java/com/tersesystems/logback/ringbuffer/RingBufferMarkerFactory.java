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
import org.slf4j.Marker;

/**
 * A marker factory that contains a ringbuffer and two inner classes, RecordMarker and DumpMarker.
 *
 * A logging statement that has record marker added will be appended to the
 *
 * @param <LoggingEventT> the logging event type
 */
public class RingBufferMarkerFactory<LoggingEventT extends ILoggingEvent> {
    private final RingBuffer<LoggingEventT> ringBuffer;

    public RingBufferMarkerFactory(int capacity) {
        this.ringBuffer = new RingBuffer<>(capacity);
    }

    Marker createDumpMarker() {
        return new DumpMarker();
    }

    Marker createRecordMarker() {
        return new RecordMarker();
    }

    class RecordMarker extends AbstractRingBufferMarker<LoggingEventT> {
        static final String TS_RECORD_MARKER = "TS_RECORD_MARKER";

        RecordMarker() {
            super(TS_RECORD_MARKER, () -> ringBuffer);
        }
    }

    class DumpMarker extends AbstractRingBufferMarker<LoggingEventT> {
        static final String TS_RECORD_MARKER = "TS_DUMP_MARKER";

        DumpMarker() {
            super(TS_RECORD_MARKER, () -> ringBuffer);
        }
    }

}
