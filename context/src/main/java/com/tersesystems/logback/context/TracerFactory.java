package com.tersesystems.logback.context;

import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class TracerFactory {

    public static final String TRACER_NAME = "TRACER";

    private static class InstanceHolder {
        static TracerFactory instance = new TracerFactory();
    }

    public static TracerFactory getInstance() {
        return InstanceHolder.instance;
    }

    private final IMarkerFactory markerFactory;
    private final Marker tracerMarker;

    public TracerFactory(IMarkerFactory markerFactory) {
        this.markerFactory = MarkerFactory.getIMarkerFactory();
        this.tracerMarker = markerFactory.getMarker(TRACER_NAME);
    }

    public TracerFactory() {
        this(MarkerFactory.getIMarkerFactory());
    }

    public Marker createTracer() {
        // always create a new marker using "new BasicMarker(name)"
        Marker marker = markerFactory.getDetachedMarker("dynamic");
        return createTracer(marker);
    }

    public <T extends Marker> T createTracer(T marker) {
        marker.add(tracerMarker);
        return marker;
    }
}
