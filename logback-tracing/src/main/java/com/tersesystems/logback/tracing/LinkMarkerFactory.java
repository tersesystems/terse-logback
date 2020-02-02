package com.tersesystems.logback.tracing;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;

public class LinkMarkerFactory {

  // Java API
  public LogstashMarker create(LinkInfo linkInfo) {
    LogstashMarker[] markers = generateMarkers(linkInfo);
    return Markers.aggregate(markers);
  }

  // Scala API
  public LogstashMarker apply(LinkInfo linkInfo) {
    return create(linkInfo);
  }

  protected LogstashMarker[] generateMarkers(LinkInfo linkInfo) {
    // XXX Should have a field name registry that lets you define field names by dataset
    LogstashMarker traceIdMarker = Markers.append("trace.trace_id", linkInfo.traceId());
    LogstashMarker parentIdMarker = Markers.append("trace.parent_id", linkInfo.parentId());
    LogstashMarker linkedSpanMarker = Markers.append("trace.link.span_id", linkInfo.linkedSpanId());
    LogstashMarker linkedTraceMarker =
        Markers.append("trace.link.trace_id", linkInfo.linkedTraceId());
    LogstashMarker spanTypeMarker = Markers.append("meta.span_type", "link");
    LogstashMarker[] markers = {
      parentIdMarker, traceIdMarker, linkedSpanMarker, linkedTraceMarker, spanTypeMarker
    };
    return markers;
  }
}
