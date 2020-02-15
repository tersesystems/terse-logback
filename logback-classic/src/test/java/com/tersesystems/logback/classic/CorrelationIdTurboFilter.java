package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.core.StreamUtils;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.spi.MDCAdapter;

/** Tells the tap filter to create an event and append it if a correlation id is found. */
public class CorrelationIdTurboFilter extends TurboFilter {
  private String mdcKey = "correlation_id";

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    // If there's a correlation id marker somewhere in the hierarchy, then good.
    Stream<Marker> markerStream = StreamUtils.fromMarker(marker);
    if (markerStream.anyMatch(m -> m instanceof CorrelationIdMarker)) {
      return FilterReply.ACCEPT;
    }

    // Look in MDC for a correlation id as well...
    Map<String, String> mdcPropertyMap = getMDCPropertyMap();
    String mdcKey = getMdcKey();
    if (mdcKey != null) {
      if (mdcPropertyMap.containsKey(mdcKey)) {
        return FilterReply.ACCEPT;
      }
    }

    // Otherwise no.
    return FilterReply.DENY;
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
