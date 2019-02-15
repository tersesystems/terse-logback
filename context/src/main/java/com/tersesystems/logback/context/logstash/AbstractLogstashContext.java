package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.TracerFactory;
import com.tersesystems.logback.context.AbstractContext;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.Map;

public abstract class AbstractLogstashContext<C extends Context<LogstashMarker, C>> extends AbstractContext<LogstashMarker, C> {

    protected AbstractLogstashContext(Map<?, ?> entries, boolean tracer) {
        super(entries, tracer);
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            return Markers.appendEntries(entries()).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries());
        }
    }
}
