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
package com.tersesystems.logback.context;

import org.slf4j.Marker;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

// Immutable class, create multiple contexts and use "and" to aggregate more context.
public abstract class AbstractContext<
        MarkerT extends Marker,
        SelfT extends Context<MarkerT, SelfT>
        > implements Context<MarkerT, SelfT> {

    private final Map<?, ?> entries;

    protected AbstractContext(Map<?, ?> entries) {
        this.entries = entries;
    }

    /**
     * @return an unmodifiable map of entries.
     */
    public Map<?, ?> entries() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public String toString() {
        String result = entries.entrySet().stream().map(entry ->
                        String.join("=",
                                entry.getKey().toString(),
                                entry.getValue().toString()))
                .collect(Collectors.joining(","));
        return this.getClass().getSimpleName() + "(" + result + ")";
    }
}
