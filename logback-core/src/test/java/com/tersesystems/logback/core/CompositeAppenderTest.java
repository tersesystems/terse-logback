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
package com.tersesystems.logback.core;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.URL;
import org.junit.Test;

public class CompositeAppenderTest {

  @Test
  public void testSimpleAppender() throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource("/logback-with-composite-appender.xml");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);

    ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
    CompositeAppender<ILoggingEvent> composite =
        (CompositeAppender<ILoggingEvent>) logger.getAppender("CONSOLE_AND_FILE");
    ListAppender<ILoggingEvent> file = (ListAppender<ILoggingEvent>) composite.getAppender("FILE");
    ListAppender<ILoggingEvent> console =
        (ListAppender<ILoggingEvent>) composite.getAppender("CONSOLE");

    logger.info("hello world");
    assertThat(file.list.get(0).getMessage()).isEqualTo("hello world");
    assertThat(console.list.get(0).getMessage()).isEqualTo("hello world");
  }
}
