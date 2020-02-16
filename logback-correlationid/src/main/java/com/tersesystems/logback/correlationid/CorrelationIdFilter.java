package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.Optional;

public class CorrelationIdFilter extends Filter<ILoggingEvent> {
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
  public FilterReply decide(ILoggingEvent event) {
    Optional<CorrelationIdProvider> maybeCorrelationId = utils.getProvider(event.getMDCPropertyMap(), event.getMarker());
    return maybeCorrelationId.isPresent() ? FilterReply.ACCEPT : FilterReply.DENY;
  }
}
