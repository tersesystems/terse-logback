package com.tersesystems.logback.context;

import net.logstash.logback.marker.LogstashMarker;

public interface Context {
    Context and(Context context);

    LogstashMarker asMarker();

    Context withTracer();

    boolean isTracingEnabled();
}
