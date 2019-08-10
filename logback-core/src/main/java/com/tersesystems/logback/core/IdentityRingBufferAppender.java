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
package com.tersesystems.logback.core;

/**
 * A cyclic buffer appender that adds the event itself to the ring buffer.
 *
 * @param <E>
 */
public class IdentityRingBufferAppender<E> extends AbstractRingBufferAppender<E, E> {
    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        ringBuffer.add(eventObject);
    }
}
