package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.tersesystems.logback.core.StreamUtils;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.spi.MDCAdapter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CorrelationIdUtils {

  private final String mdcKey;

  public CorrelationIdUtils() {
    mdcKey = null;
  }

  public CorrelationIdUtils(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  public Optional<String> get(Map<String, String> mdcPropertyMap, Marker marker) {
    return getProvider(mdcPropertyMap, marker).map(CorrelationIdProvider::getCorrelationId);
  };

  public Optional<String> get(Map<String, String> mdcPropertyMap) {
    return getProvider(mdcPropertyMap).map(CorrelationIdProvider::getCorrelationId);
  };

  public Optional<CorrelationIdProvider> getProvider(Map<String, String> mdcPropertyMap, Marker marker) {
    Stream<Marker> markerStream = StreamUtils.fromMarker(marker);
    Optional<CorrelationIdProvider> first =
        markerStream
            .filter(m -> m instanceof CorrelationIdMarker)
            .map(m -> (CorrelationIdProvider) m)
            .findFirst();

    if (first.isPresent()) {
      return first;
    } else {
      return getProvider(mdcPropertyMap);
    }
  }

  public Optional<CorrelationIdProvider> getProvider(Map<String, String> mdcPropertyMap) {
    // Look in MDC for a correlation id as well...
    if (mdcKey != null) {
      String s = mdcPropertyMap.get(mdcKey);
      if (s != null) {
        return Optional.of(() -> s);
      }
    }
    return Optional.empty();
  }

  public Map<String, String> getMDCPropertyMap() {
    MDCAdapter mdc = MDC.getMDCAdapter();

    Map<String, String> mdcPropertyMap;
    if (mdc instanceof LogbackMDCAdapter)
      mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
    else mdcPropertyMap = mdc.getCopyOfContextMap();

    // mdcPropertyMap still null, use emptyMap()
    if (mdcPropertyMap == null) mdcPropertyMap = Collections.emptyMap();

    return mdcPropertyMap;
  }

}
