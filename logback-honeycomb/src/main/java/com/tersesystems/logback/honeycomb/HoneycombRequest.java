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
package com.tersesystems.logback.honeycomb;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class HoneycombRequest {

    private final Integer sampleRate;
    private final ILoggingEvent event;

    public HoneycombRequest(Integer sampleRate, ILoggingEvent event) {
        this.sampleRate = sampleRate;
        this.event = event;
    }

    public ILoggingEvent getEvent() {
        return event;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public String getTimestamp() {
        return formatEvent(Instant.ofEpochMilli(event.getTimeStamp()));
    }

    private String formatEvent(Instant eventTime) {
        return DateTimeFormatter.ISO_INSTANT.format(eventTime);
    }
}
