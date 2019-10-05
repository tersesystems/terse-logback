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
package com.tersesystems.logback.sigar;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class SigarTest {

  @Test
  public void testWithDump() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory();

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.error("I am very much under load");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(1);
  }

  LoggerContext createLoggerFactory() throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource("/logback-sigar.xml");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
    Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
    return (ListAppender<ILoggingEvent>) requireNonNull(root.getAppender("LIST"));
  }
}
