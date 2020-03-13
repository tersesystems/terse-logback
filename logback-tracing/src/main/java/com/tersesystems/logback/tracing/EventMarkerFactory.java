/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */

package com.tersesystems.logback.tracing;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

/**
 * This is a marker factory that adds several logstash markers to create a span in Honeycomb format.
 */
public class EventMarkerFactory {

  // Java API
  public LogstashMarker create(EventInfo eventInfo) {
    LogstashMarker[] markers = generateMarkers(eventInfo);
    return Markers.aggregate(markers);
  }

  // Scala API
  public LogstashMarker apply(EventInfo eventInfo) {
    return create(eventInfo);
  }

  protected LogstashMarker[] generateMarkers(EventInfo eventInfo) {
    // XXX Should have a field name registry that lets you define field names by dataset
    LogstashMarker nameMarker = Markers.append("name", eventInfo.name());
    LogstashMarker parentIdMarker = Markers.append("trace.parent_id", eventInfo.parentId());
    LogstashMarker traceIdMarker = Markers.append("trace.trace_id", eventInfo.traceId());
    LogstashMarker spanTypeMarker = Markers.append("meta.span_type", "span_event");
    // Don't include the timestamp marker, as it'll be handled by Logback
    LogstashMarker[] markers = {nameMarker, parentIdMarker, traceIdMarker, spanTypeMarker};
    return markers;
  }
}
