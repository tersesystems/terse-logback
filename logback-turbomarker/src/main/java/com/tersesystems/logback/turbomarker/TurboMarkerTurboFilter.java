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

/**
 * This class is a turbo filter that hands off the evaluation of whether a logging event should be created to the
 * marker, if it is a predicate marker.
 */
public class TurboMarkerTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        if (evaluateMarker(rootMarker, rootMarker, logger, level, params, t)) {
            return FilterReply.ACCEPT;
        }

        return stream(rootMarker)
                .filter(m -> evaluateMarker(m, rootMarker, logger, level, params, t))
                .findFirst()
                .map(m -> FilterReply.ACCEPT)
                .orElse(FilterReply.NEUTRAL);
    }

    private boolean evaluateMarker(Marker marker, Marker rootMarker, Logger logger, Level level, Object[] params, Throwable t) {
        if (marker instanceof TurboMarker) {
            TurboMarker turboMarker = (TurboMarker) marker;
            return turboMarker.test(rootMarker, logger, level, params, t);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Stream<Marker> stream(Marker marker) {
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(marker.iterator(), 0);
        return (Stream<Marker>) StreamSupport.stream(spliterator, false);
    }
}
