package com.tersesystems.logback.classic;

import org.slf4j.Marker;

/** A very simple correlation id marker. */
public interface CorrelationIdMarker extends Marker {
  String getCorrelationId();

  static CorrelationIdMarker create(String value) {
    return new CorrelationIdBasicMarker(value);
  }
}

/** Implementation of correlation id. */
class CorrelationIdBasicMarker extends TerseBasicMarker implements CorrelationIdMarker {
  private final String value;

  public CorrelationIdBasicMarker(String value) {
    super("TS_CORRELATION_ID");
    this.value = value;
  }

  public String getCorrelationId() {
    return value;
  }
}
