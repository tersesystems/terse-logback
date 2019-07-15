package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.function.Function;

@FunctionalInterface
public interface LoggerDecider extends Function<Logger, FilterReply>, TurboFilterDecider {
    default FilterReply decide(Marker marker, Logger logger, ch.qos.logback.classic.Level level, String format, Object[] params, Throwable t) {
        return apply(logger);
    }
}
