/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.classic;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.TurboFilterList;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import java.net.URL;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class TapFilterTest {

  @Test
  public void testSimple() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-tapfilter.xml");

    // Write something that never gets logged explicitly...
    Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
    debugLogger.debug("debug one");
    debugLogger.debug("debug two");
    debugLogger.debug("debug three");
    debugLogger.debug("debug four");

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.error("Write out error message to console");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(5);
  }

  @Test
  public void testCorrelationWithNoMarker() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-tapfilter-correlation.xml");

    // Because there's no correlation id, it should never make it past the filter here.
    Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
    debugLogger.debug("debug one");
    debugLogger.debug("debug two");
    debugLogger.debug("debug three");
    debugLogger.debug("debug four");

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.error("Write out error message to console");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(0);
  }

  @Test
  public void testCorrelationWithMarker() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-tapfilter-correlation.xml");

    CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create("12345");

    // Because there's no correlation id, it should never make it past the filter here.
    Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
    debugLogger.debug(correlationIdMarker, "debug one");
    debugLogger.debug(correlationIdMarker, "debug two");
    debugLogger.debug("debug three");
    debugLogger.debug("debug four");

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.error("Write out error message to console");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(2);
  }

  @Test
  public void testCorrelationWithMDC() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-tapfilter-correlation.xml");

    MDC.put("correlationId", "12345");
    CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create("12345");

    // Because there's no correlation id, it should never make it past the filter here.
    Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
    debugLogger.debug(correlationIdMarker, "debug one");
    debugLogger.debug(correlationIdMarker, "debug two");
    debugLogger.debug("debug three");
    debugLogger.debug("debug four");

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.error("Write out error message to console");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(5);
  }

  LoggerContext createLoggerFactory(String resourceName) throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource(resourceName);
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  Optional<Appender<ILoggingEvent>> getFilterAppender(TurboFilterList turboFilterList) {
    return turboFilterList.stream()
        .filter(f -> f instanceof AppenderAttachable<?>)
        .map(f -> ((AppenderAttachable<ILoggingEvent>) f).iteratorForAppenders().next())
        .findFirst();
  }

  ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
    Optional<Appender<ILoggingEvent>> maybeAppender =
        getFilterAppender(context.getTurboFilterList());
    if (maybeAppender.isPresent()) {
      return (ListAppender<ILoggingEvent>) requireNonNull(maybeAppender.get());
    } else {
      throw new IllegalStateException("Cannot find appender");
    }
  }
}
