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
import com.tersesystems.logback.classic.TerseBasicMarker;

import java.util.function.Supplier;

/**
 * Marker with a reference to a ring buffer.
 */
public abstract class AbstractRingBufferMarker<E extends ILoggingEvent> extends TerseBasicMarker implements RingBufferAware<E> {

    // Use a supplier here so we have more flexibility when accessing the ring buffer.
    private final Supplier<RingBuffer<E>> supplier;

    AbstractRingBufferMarker(String name, Supplier<RingBuffer<E>> supplier) {
        super(name);
        this.supplier = supplier;
    }

    @Override
    public RingBuffer<E> getRingBuffer() {
        return supplier.get();
    }
}
