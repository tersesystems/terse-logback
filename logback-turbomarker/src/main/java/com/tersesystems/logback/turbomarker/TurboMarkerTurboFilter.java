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
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * This class is a turbo filter that hands off the evaluation of whether a logging event should be created to the
 * marker, if it is a predicate marker.
 */
public class TurboMarkerTurboFilter extends TurboFilter implements TurboFilterDecider {

    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (rootMarker == null) {
            return FilterReply.NEUTRAL;
        }

        if (evaluateMarker(rootMarker, rootMarker, logger, level, format, params, t) == FilterReply.ACCEPT) {
            return FilterReply.ACCEPT;
        }

        return stream(rootMarker)
                .map(m -> evaluateMarker(m, rootMarker, logger, level, format, params, t))
                .filter(reply -> reply != FilterReply.NEUTRAL)
                .findFirst()
                .orElse(FilterReply.NEUTRAL);
    }

    private FilterReply evaluateMarker(Marker marker, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (marker instanceof TurboFilterDecider) {
            TurboFilterDecider decider = (TurboFilterDecider) marker;
            return decider.decide(rootMarker, logger, level, format, params, t);
        }
        return FilterReply.NEUTRAL;
    }

    @SuppressWarnings("unchecked")
    private Stream<Marker> stream(Marker marker) {
        requireNonNull(marker);
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(marker.iterator(), 0);
        return (Stream<Marker>) StreamSupport.stream(spliterator, false);
    }
}
