/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */

package com.tersesystems.logback.correlationid;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class CorrelationIdFilterTest {
  @Before
  @After
  public void clearMDC() {
    MDC.clear();
  }

  @Test
  public void testFilter() throws JoranException {
    MDC.clear();
    LoggerContext loggerFactory = createLoggerFactory("/logback-correlationid.xml");

    // Write something that never gets logged explicitly...
    Logger logger = loggerFactory.getLogger("com.example.Debug");
    String correlationId = "12345";
    CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create(correlationId);

    // should be logged because marker
    logger.info(correlationIdMarker, "info one");

    logger.info("info two"); // should not be logged

    // Everything below this point should be logged.
    MDC.put("correlationId", correlationId);
    logger.info("info three");
    logger.info(correlationIdMarker, "info four");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(3);
  }

  LoggerContext createLoggerFactory(String resourceName) throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource(resourceName);
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
    Optional<Appender<ILoggingEvent>> maybeAppender =
        getFirstAppender(context.getLogger(Logger.ROOT_LOGGER_NAME));
    if (maybeAppender.isPresent()) {
      return (ListAppender<ILoggingEvent>) requireNonNull(maybeAppender.get());
    } else {
      throw new IllegalStateException("Cannot find appender");
    }
  }

  private Optional<Appender<ILoggingEvent>> getFirstAppender(Logger logger) {
    for (Iterator<Appender<ILoggingEvent>> iter = logger.iteratorForAppenders(); iter.hasNext(); ) {
      Appender<ILoggingEvent> next = logger.iteratorForAppenders().next();
      return Optional.of(next);
    }
    return Optional.empty();
  }
}
