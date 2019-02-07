package com.tersesystems.logback.context;

import com.tersesystems.logback.TracerFactory;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.Collections;
import java.util.Map;

public class LogstashContext extends AbstractContext<LogstashMarker> {

    public LogstashContext(Map<?, ?> entries, boolean t) {
        super(entries, t);
    }

    @Override
    public Context<LogstashMarker> withTracer() {
        return create(entries(), true);
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            return Markers.appendEntries(entries()).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries());
        }
    }

    @Override
    public Context<LogstashMarker> create(Map<?, ?> entries, boolean tracing) {
        return new LogstashContext(entries, tracing);
    }

    public static Context<LogstashMarker> create(Map<?, ?> entries) {
        return new LogstashContext(entries, false);
    }

    public static Context<LogstashMarker> create(Object key, Object value) {
        return new LogstashContext(Collections.singletonMap(key, value), false);
    }

    public static Context<LogstashMarker> create() {
        return create(Collections.emptyMap());
    }
}
