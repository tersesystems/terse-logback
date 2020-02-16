package com.tersesystems.logback.correlationid;

/** Provides a correlation id. */
public interface CorrelationIdProvider {
  String getCorrelationId();
}
