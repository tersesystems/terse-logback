package com.tersesystems.logback.context;

import org.slf4j.Marker;

import java.util.Map;

public interface Context<T extends Marker> {
    Context<T> and(Context<Marker> context);

    T asMarker();

    Context<T> withTracer();

    boolean isTracingEnabled();

    Map<?,?> entries();
}
