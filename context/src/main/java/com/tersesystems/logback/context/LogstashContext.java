package com.tersesystems.logback.context;

import com.tersesystems.logback.TracerFactory;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogstashContext extends AbstractContext<LogstashMarker> {

    private LogstashContext(Map<?, ?> entries, boolean t) {
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
    public Context<LogstashMarker> and(Context<Marker> context) {
        boolean tracing = this.isTracingEnabled() || context.isTracingEnabled();
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(context.entries());
        return new LogstashContext(mergedEntries, tracing);
    }

    public static Context<LogstashMarker> create(Map<?, ?> entries) {
        return new LogstashContext(entries, false);
    }

    public static Context<LogstashMarker> create(Object key, Object value) {
        return create(Collections.singletonMap(key, value));
    }

    public static Context<LogstashMarker> create() {
        return create(Collections.emptyMap());
    }
}
