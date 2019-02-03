package com.tersesystems.logback.proxy;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.util.Collections;

// Immutable class (as long as you don't futz with logstashMarker)
public class LogstashMarkerContext implements MarkerContext {

    private final LogstashMarker logstashMarker;

    private LogstashMarkerContext(LogstashMarker logstashMarker) {
        this.logstashMarker = logstashMarker;
    }

    @Override
    public MarkerContext withMarker(Marker marker) {
        return new LogstashMarkerContext(this.logstashMarker.and(logstashMarker));
    }

    @Override
    public MarkerContext and(MarkerContext markerContext) {
        return withMarker(((LogstashMarkerContext) markerContext).logstashMarker);
    }

    @Override
    public Marker asMarker() {
        return this.logstashMarker;
    }

    public static LogstashMarkerContext create() {
        return create(emptyMarker());
    }

    public static LogstashMarkerContext create(Marker marker) {
        if (marker instanceof LogstashMarker) {
            return new LogstashMarkerContext((LogstashMarker) marker);
        }
        LogstashMarker holder = emptyMarker();
        holder.add(marker);
        return new LogstashMarkerContext(holder);
    }

    private static LogstashMarker emptyMarker() {
        return Markers.appendEntries(Collections.emptyMap());
    }

    @Override
    public String toString() {
        return "LogstashMarkerContext(" + logstashMarker.toString() + ")";
    }
}
