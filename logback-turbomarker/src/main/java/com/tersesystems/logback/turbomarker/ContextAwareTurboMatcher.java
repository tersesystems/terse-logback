package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.Marker;

/**
 * Provides a context aware matcher.
 *
 * This is intended to be used by long-lived services, whereas markers are very ephemeral.
 *
 * @param <C> the application's context.
 */
public interface ContextAwareTurboMatcher<C> {
    boolean match(ContextAwareTurboMarker marker, C context, Marker rootMarker, Logger logger, Level level, Object[] params, Throwable t);
}
