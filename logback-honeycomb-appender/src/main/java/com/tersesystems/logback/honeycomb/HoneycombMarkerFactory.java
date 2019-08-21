/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.honeycomb;

import com.tersesystems.logback.honeycomb.client.SpanInfo;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

/**
 * This is a marker that adds several logstash markers to itself as references, using the
 * honeycomb format.
 */
public class HoneycombMarkerFactory {

    public LogstashMarker create(SpanInfo spanInfo) {
        LogstashMarker[] markers = generateMarkers(spanInfo);
        return Markers.aggregate(markers);
    }

    protected LogstashMarker[] generateMarkers(SpanInfo spanInfo) {
        LogstashMarker nameMarker = Markers.append("name", spanInfo.name());
        LogstashMarker spanIdMarker = Markers.append("trace.span_id", spanInfo.spanId());
        LogstashMarker parentIdMarker = Markers.append("trace.parent_id", spanInfo.parentId());
        LogstashMarker traceIdMarker = Markers.append("trace.trace_id", spanInfo.traceId());
        LogstashMarker serviceNameMarker = Markers.append("service_name", spanInfo.serviceName());
        LogstashMarker durationMs = Markers.append("duration_ms", spanInfo.duration().toMillis());

        LogstashMarker[] markers = {
          nameMarker, spanIdMarker, parentIdMarker, traceIdMarker, serviceNameMarker, durationMs
        };
        return markers;
    }

}
