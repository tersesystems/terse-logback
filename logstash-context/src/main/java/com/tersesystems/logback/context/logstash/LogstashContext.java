package com.tersesystems.logback.context.logstash;

import com.tersesystems.logback.context.Context;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *A context backed by logstash markers.
 */
public class LogstashContext extends AbstractLogstashContext<LogstashContext> {

    protected LogstashContext(Map<?, ?> entries) {
        super(entries);
    }

    @Override
    public LogstashContext withTracer() {
        return create(entries(), true);
    }

    @Override
    public LogstashContext and(Context<? extends Marker, ?> context) {
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(context.entries());
        return new LogstashContext(mergedEntries);
    }

    public static LogstashContext create(Map<?, ?> entries) {
        return new LogstashContext(entries);
    }

    public static LogstashContext create(Object key, Object value) {
        return create(Collections.singletonMap(key, value));
    }

    public static LogstashContext create() {
        return create(Collections.emptyMap());
    }
}
