package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.function.BiFunction;

@FunctionalInterface
public interface FormatParamsDecider extends BiFunction<String, Object[], FilterReply>, TurboFilterDecider {
    @Override
    default FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return apply(format, params);
    }
}
