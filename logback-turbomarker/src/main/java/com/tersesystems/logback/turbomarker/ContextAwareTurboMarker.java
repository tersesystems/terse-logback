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
package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.Marker;

import static java.util.Objects.requireNonNull;

/**
 * This class passes through a custom application context and a matcher, which makes the ultimate decision.
 *
 * @param <C> the context of the predicate marker.
 * @param <M> The matcher containing logic.
 */
public abstract class ContextAwareTurboMarker<C, M extends ContextAwareTurboMatcher<C>> extends TurboMarker {

    private final C context;
    private final M matcher;

    public ContextAwareTurboMarker(String name, C context, M matcher) {
        super(name);
        this.context = requireNonNull(context);
        this.matcher = matcher;
    }

    M getMatcher() {
        return matcher;
    }

    C getContext() {
        return context;
    }

    @Override
    public boolean test(Marker rootMarker, Logger logger, Level level, Object[] params, Throwable t) {
        return matcher.match(this, context, rootMarker, logger, level, params, t);
    }

}
