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
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import static java.util.Objects.requireNonNull;

/**
 * This class passes through a custom application context and a matcher, which makes the ultimate decision.
 *
 * @param <C> the context of the predicate marker.
 */
public class ContextAwareTurboMarker<C> extends TurboMarker implements TurboFilterDecider {

    private final C context;
    private final ContextAwareTurboFilterDecider<C> contextAwareDecider;

    public ContextAwareTurboMarker(String name, C context, ContextAwareTurboFilterDecider<C> decider) {
        super(name);
        this.context = requireNonNull(context);
        this.contextAwareDecider = requireNonNull(decider);
    }

    ContextAwareTurboFilterDecider<C> getContextAwareDecider() {
        return contextAwareDecider;
    }

    C getContext() {
        return context;
    }

    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return contextAwareDecider.decide(this, context, rootMarker, logger, level, format, params, t);
    }
}
