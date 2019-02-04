package com.tersesystems.logback.proxy;

import com.tersesystems.logback.TracerFactory;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

// Immutable class, create multiple contexts and use "and" to aggregate more context.
public class ContextImpl implements Context {

    private final Map<?, ?> entries;
    private final boolean tracing;

    private ContextImpl(Map<?, ?> entries, boolean tracing) {
        this.entries = entries; this.tracing = tracing;
    }

    @Override
    public Context and(Context context) {
        boolean t = this.isTracingEnabled() || context.isTracingEnabled();
        return new ContextImpl(((ContextImpl) context).entries, t);
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            // Dependency on "classic" here...
            return Markers.appendEntries(entries).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries);
        }
    }

    @Override
    public Context withTracer() {
        return new ContextImpl(entries, true);
    }

    @Override
    public boolean isTracingEnabled() {
        return tracing;
    }

    public static ContextImpl create() {
        return create(Collections.emptyMap());
    }

    public static ContextImpl create(Map<?, ?> entries) {
        return new ContextImpl(entries, false);
    }

    public static ContextImpl create(Object key, Object value) {
        return new ContextImpl(Collections.singletonMap(key, value), false);
    }

    @Override
    public String toString() {
        String result = entries.entrySet().stream().map(entry ->
                        String.join("=",
                                entry.getKey().toString(),
                                entry.getValue().toString()))
                .collect(Collectors.joining(","));
        return "ContextImpl(" + result + ")";
    }
}
