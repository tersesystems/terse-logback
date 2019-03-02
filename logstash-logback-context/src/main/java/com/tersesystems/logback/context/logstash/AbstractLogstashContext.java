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
package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.AbstractContext;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.Map;

/**
 * Helper class that fixes the marker as LogstashMarker and adds an `asMarker` method.
 *
 * @param <C> the type of context.
 */
public abstract class AbstractLogstashContext<C extends Context<LogstashMarker, C>> extends AbstractContext<LogstashMarker, C> {

    protected AbstractLogstashContext(Map<?, ?> entries) {
        super(entries);
    }

    @Override
    public LogstashMarker asMarker() {
        return Markers.appendEntries(entries());
    }
}
