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

import com.tersesystems.logback.classic.TerseBasicMarker;

import java.time.Instant;
import java.util.Objects;

public class StartTimeMarker extends TerseBasicMarker implements StartTimeSupplier {
    private static final String TIMESTAMP_MARKER_NAME = "TS_TIMESTAMP_MARKER";
    private final Instant startTime;

    public StartTimeMarker(Instant start) {
        super(TIMESTAMP_MARKER_NAME);
        this.startTime = Objects.requireNonNull(start);
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }
}
