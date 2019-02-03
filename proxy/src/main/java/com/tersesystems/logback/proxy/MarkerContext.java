package com.tersesystems.logback.proxy;

import org.slf4j.Marker;

public interface MarkerContext {
    MarkerContext withMarker(Marker marker);

    MarkerContext and(MarkerContext logstashMarkerContext);

    Marker asMarker();
}
