package com.tersesystems.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.StreamUtils;

import java.time.Instant;
import java.util.Optional;

public class StartTime {

    // If this is a span, then we want to register the START of the span,
    // rather than when the logging event occurred (which is the END of
    // the span).  So we look for a special marker that overrides
    // the given timestamp.
    public static Optional<Instant> fromOptional(ILoggingEvent event) {
        return StreamUtils.fromMarker(event.getMarker())
                .filter(marker -> marker instanceof StartTimeSupplier)
                .map(marker -> (StartTimeSupplier) marker)
                .map(StartTimeSupplier::getStartTime)
                .findFirst();
    }

    public static Instant from(ILoggingEvent eventObject) {
        return fromOptional(eventObject).orElse(Instant.ofEpochMilli(eventObject.getTimeStamp()));
    }

}
