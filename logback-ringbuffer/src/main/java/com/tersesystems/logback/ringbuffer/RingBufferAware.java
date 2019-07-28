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

/**
 * An interface that has access to a ring buffer.
 *
 * @param <ElementT> the element type.
 */
public interface RingBufferAware<ElementT> {
    RingBuffer<ElementT> getRingBuffer();
}
