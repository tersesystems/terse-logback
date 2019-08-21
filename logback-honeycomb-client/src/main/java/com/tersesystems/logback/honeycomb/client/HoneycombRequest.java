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
package com.tersesystems.logback.honeycomb.client;

import java.time.Instant;

public class HoneycombRequest<E> {

    private final Integer sampleRate;
    private final E event;
    private final Instant timestamp;

    public HoneycombRequest(Integer sampleRate, Instant timestamp, E event) {
        this.sampleRate = sampleRate;
        this.timestamp = timestamp;
        this.event = event;
    }

    public E getEvent() {
        return event;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
