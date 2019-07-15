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
package com.tersesystems.logback.turbomarker;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMarkerTest {

    @Test
    public void testMatchingUserMarker() throws JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        String userId = "28";
        ApplicationContext applicationContext = new ApplicationContext(userId);
        UserMarkerFactory userMarkerFactory = new UserMarkerFactory();
        userMarkerFactory.addUserId(userId); // say we want logging events created for this user id

        UserMarker userMarker = userMarkerFactory.create(applicationContext);

        logger.info(userMarker, "Hello world, I am info");
        logger.debug(userMarker, "Hello world, I am debug");

        ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
        assertThat(appender.list.size()).isEqualTo(2);
    }

    @Test
    public void testNonMatchingUserMarker() throws JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        String userId = "28";
        ApplicationContext applicationContext = new ApplicationContext(userId);
        UserMarkerFactory userMarkerFactory = new UserMarkerFactory();
        UserMarker userMatchMarker = userMarkerFactory.create(applicationContext);

        logger.info(userMatchMarker, "Hello world, I am info");
        logger.debug(userMatchMarker, "Hello world, I am debug");

        ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
        assertThat(appender.list.size()).isEqualTo(0);
    }
}
