package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.tersesystems.logback.classic.TerseBasicMarker;
import org.slf4j.Marker;

/**
 * This class is a marker that can test to see whether an event should be allowed through a turbo filter.
 */
public abstract class TurboMarker extends TerseBasicMarker {
    public TurboMarker(String name) {
        super(name);
    }

    public abstract boolean test(Marker rootMarker, Logger logger, Level level, Object[] params, Throwable t);
}
