package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.TurboFilterList;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.ILoggingEventFactory;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.classic.TurboFilterDecider;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import org.slf4j.Marker;

/**
 * A tap filter is used to tap some amount of incoming process and pass them to a specially
 * configured appender even if they do not qualify as a logging event under normal circumstances.
 * This is a <a
 * href="https://www.enterpriseintegrationpatterns.com/patterns/messaging/WireTap.html">wiretap</a>
 * pattern from Enterprise Integration Patterns.
 *
 * <p>It completely bypasses any kind of logging level configured on the front end, so you can set a
 * logger to INFO level but still have access to all TRACE events when an error occurs, through the
 * tap filter's appenders.
 *
 * <p>NOTE: This means that isLoggingTrace etc always returns true.
 */
public class TapFilter extends TurboFilter
    implements DefaultAppenderAttachable<ILoggingEvent>, TurboFilterDecider {

  private final AppenderAttachableImpl<ILoggingEvent> aae = new AppenderAttachableImpl<>();

  private ILoggingEventFactory<ILoggingEvent> loggingEventFactory;

  private TurboFilterList evaluatorList = new TurboFilterList();

  public void addTurboFilter(TurboFilter turboFilter) {
    evaluatorList.add(turboFilter);
  }

  public TurboFilterList getTurboFilters() {
    return evaluatorList;
  }

  public void getTurboFilters(TurboFilterList tapEvaluator) {
    this.evaluatorList = tapEvaluator;
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return aae;
  }

  public ILoggingEventFactory<ILoggingEvent> getLoggingEventFactory() {
    return loggingEventFactory;
  }

  public void setLoggingEventFactory(ILoggingEventFactory<ILoggingEvent> loggingEventFactory) {
    this.loggingEventFactory = loggingEventFactory;
  }

  @Override
  public void start() {
    if (this.loggingEventFactory == null) {
      this.loggingEventFactory = new LoggingEventFactory();
    }

    if (evaluatorList.isEmpty()) {
      TurboFilter acceptAllTurboFilter =
          new TurboFilter() {
            @Override
            public FilterReply decide(
                Marker marker,
                Logger logger,
                Level level,
                String format,
                Object[] params,
                Throwable t) {
              return FilterReply.ACCEPT;
            }
          };
      evaluatorList.add(acceptAllTurboFilter);
    }

    super.start();
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    // Called by isLoggingTrace() -- there's no actual message here.
    if (logger != null && format == null && params == null) {
      // Need to turn on events for everything so that we can cover conditional events.
      return FilterReply.ACCEPT;
    }

    // Only tap if the internal filters pass.
    FilterReply turboFilterChainDecision =
        evaluatorList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
    if (turboFilterChainDecision.equals(FilterReply.ACCEPT)) {
      ILoggingEvent loggingEvent =
          loggingEventFactory.create(marker, logger, level, format, params, t);
      // initialize the mdc in the logging event...
      loggingEvent.prepareForDeferredProcessing();
      // For every message that is acceptable, store it in the appender and return.
      aae.appendLoopOnAppenders(loggingEvent);
    }
    return FilterReply.NEUTRAL;
  }
}
