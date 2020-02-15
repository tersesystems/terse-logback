package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.Optional;
import org.slf4j.Marker;

/** Tells the tap filter to create an event and append it if a correlation id is found. */
public class CorrelationIdTurboFilter extends TurboFilter {
  private String mdcKey = "correlation_id";

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  protected CorrelationIdUtils utils;

  @Override
  public void start() {
    super.start();
    utils = new CorrelationIdUtils(mdcKey);
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    Optional<CorrelationIdProvider> maybeCorrelationId = utils.getProvider(marker);
    return maybeCorrelationId.isPresent() ? FilterReply.ACCEPT : FilterReply.DENY;
  }
}
