package com.tersesystems.logback.classic.ringbuffer;/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.tersesystems.logback.core.RingBuffer;

import java.util.Iterator;

/**
 * An abstract appender that keeps an in-memory ring buffer of a given type.
 *
 * @param <EventT> the type of logging event
 */
public abstract class AbstractRingBufferAppender<EventT extends ILoggingEvent, EncodingT> extends AppenderBase<EventT> implements RingBufferAppender<EventT, EncodingT>, HasRingBuffer<EncodingT> {

    RingBuffer<EncodingT> ringBuffer;

    int maxSize = 512;

    public void start() {
        ringBuffer = new RingBuffer<>(maxSize);
        super.start();
    }

    public void stop() {
        ringBuffer = null;
        super.stop();
    }

    public void reset() {
        ringBuffer.clear();
    }

    public RingBuffer<EncodingT> getRingBuffer() {
        return ringBuffer;
    }

    /**
     * Set the size of the cyclic buffer.
     */
    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public Iterator<EncodingT> iterator() {
        return ringBuffer.iterator();
    }
}
