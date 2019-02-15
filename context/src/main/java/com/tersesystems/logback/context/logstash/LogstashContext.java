package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.TracerFactory;
import com.tersesystems.logback.context.AbstractContext;
import com.tersesystems.logback.context.Context;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogstashContext extends AbstractLogstashContext<LogstashContext> {

    protected LogstashContext(Map<?, ?> entries, boolean t) {
        super(entries, t);
    }

    @Override
    public LogstashContext withTracer() {
        return create(entries(), true);
    }

    @Override
    public LogstashContext and(Context<? extends Marker, ?> context) {
        boolean tracing = this.isTracingEnabled() || context.isTracingEnabled();
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(context.entries());
        return new LogstashContext(mergedEntries, tracing);
    }

    public static LogstashContext create(Map<?, ?> entries) {
        return new LogstashContext(entries, false);
    }

    public static LogstashContext create(Object key, Object value) {
        return create(Collections.singletonMap(key, value));
    }

    public static LogstashContext create() {
        return create(Collections.emptyMap());
    }
}
