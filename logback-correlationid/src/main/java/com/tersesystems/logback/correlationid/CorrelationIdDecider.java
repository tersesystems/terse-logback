package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.TurboFilterDecider;
import org.slf4j.Marker;

import java.util.Optional;

public class CorrelationIdDecider implements TurboFilterDecider {
  protected final CorrelationIdUtils utils;

  public CorrelationIdDecider(CorrelationIdUtils utils) {
    this.utils = utils;
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    Optional<CorrelationIdProvider> maybeCorrelationId = utils.getProvider(utils.getMDCPropertyMap(), marker);
    return maybeCorrelationId.isPresent() ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
  }

}
