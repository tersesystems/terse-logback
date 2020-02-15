package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.TapFilter;
import com.tersesystems.logback.classic.TurboFilterDecider;
import org.slf4j.Marker;

/** Tells the tap filter to create an event and append it if a correlation id is found. */
public class CorrelationIdTapFilter extends TapFilter {
  private String mdcKey = "correlation_id";

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  protected TurboFilterDecider decider;

  @Override
  public void start() {
    super.start();
    decider = new CorrelationIdDecider(new CorrelationIdUtils(mdcKey));
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    return decider.decide(marker, logger, level, format, params, t);
  }
}
