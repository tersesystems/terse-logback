package com.tersesystems.logback.context;

import com.tersesystems.logback.TracerFactory;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.Map;

public class LogstashContext extends AbstractContext<LogstashMarker> {

    public LogstashContext(Map<?, ?> entries, boolean t) {
        super(entries, t);
    }

    @Override
    public Context<LogstashMarker> and(Context<Marker> context) {
        boolean t = this.isTracingEnabled() || context.isTracingEnabled();
        return new LogstashContext(context.entries(), t);
    }

    @Override
    public Context<LogstashMarker> withTracer() {
        return new LogstashContext(entries(), true);
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            return Markers.appendEntries(entries()).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries());
        }
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
