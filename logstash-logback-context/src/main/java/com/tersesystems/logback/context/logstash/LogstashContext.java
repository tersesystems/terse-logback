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

import com.tersesystems.logback.context.Context;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *A context backed by logstash markers.
 */
public class LogstashContext extends AbstractLogstashContext<LogstashContext> {

    protected LogstashContext(Map<?, ?> entries) {
        super(entries);
    }

    @Override
    public LogstashContext and(Context<? extends Marker, ?> context) {
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(context.entries());
        return new LogstashContext(mergedEntries);
    }

    public static LogstashContext create(Map<?, ?> entries) {
        return new LogstashContext(entries);
    }

    public static LogstashContext create(Object key, Object value) {
        return create(Collections.singletonMap(key, value));
    }

    public static LogstashContext create() {
        return create(Collections.emptyMap());
    }
}
