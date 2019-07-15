package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.function.Function;

public interface ContextDecider<C> extends Function<C, FilterReply>, ContextAwareTurboFilterDecider<C> {
    @Override
    default FilterReply decide(ContextAwareTurboMarker<C> marker, C context, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return apply(context);
    }
}
