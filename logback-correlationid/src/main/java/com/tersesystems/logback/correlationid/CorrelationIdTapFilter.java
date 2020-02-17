package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.TapFilter;
import java.util.Map;
import org.slf4j.Marker;

/** Tells the tap filter to create an event and append it if a correlation id is found. */
public class CorrelationIdTapFilter extends TapFilter {
  private String mdcKey = "correlation_id";
  private CorrelationIdUtils utils;

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  @Override
  public void start() {
    super.start();
    utils = new CorrelationIdUtils(mdcKey);
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (logger != null && format == null && params == null) {
      // Need to turn on events for everything so that we can cover conditional events.
      return FilterReply.ACCEPT;
    }

    Map<String, String> mdcPropertyMap = utils.getMDCPropertyMap();
    if (utils.getProvider(mdcPropertyMap, marker).isPresent()) {
      ILoggingEvent loggingEvent =
          getLoggingEventFactory().create(marker, logger, level, format, params, t);
      // initialize the mdc in the logging event...
      loggingEvent.prepareForDeferredProcessing();
      // For every message that is acceptable, store it in the appender and return.
      appenderAttachableImpl().appendLoopOnAppenders(loggingEvent);
    }
    return FilterReply.NEUTRAL;
  }
}
