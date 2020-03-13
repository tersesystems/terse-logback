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
package com.tersesystems.logback.uniqueid;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.logback.core.ComponentContainer;
import com.tersesystems.logback.core.DecoratingAppender;
import java.net.URL;
import org.junit.Test;

public class UniqueIdAppenderTest {

  @Test
  public void testUniqueIdEventAppender() throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource("/logback-with-uniqueid-appender.xml");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);

    ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);

    logger.info("hello world");
    DecoratingAppender<ILoggingEvent, ILoggingEvent> appender =
        (DecoratingAppender<ILoggingEvent, ILoggingEvent>)
            logger.getAppender("DECORATE_WITH_UNIQUEID");

    ListAppender<ILoggingEvent> listAppender =
        (ListAppender<ILoggingEvent>) appender.getAppender("LIST");
    ILoggingEvent event = listAppender.list.get(0);
    ComponentContainer container = (ComponentContainer) event;
    UniqueIdProvider idComponent = container.getComponent(UniqueIdProvider.class);
    assertThat(idComponent.uniqueId()).isNotBlank();
  }
}
