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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

public class LDMarkerTest {

  @Test
  @DisplayName("Matching Marker")
  public void testMatchingMarker() {
    LDClientInterface client = Mockito.mock(LDClientInterface.class);
    when(client.boolVariation(anyString(), any(), anyBoolean())).thenReturn(true);

    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger =
        loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    LDMarkerFactory markerFactory = new LDMarkerFactory(client);
    LDUser ldUser =
        new LDUser.Builder("UNIQUE IDENTIFIER")
            .firstName("Bob")
            .lastName("Loblaw")
            .customString("groups", singletonList("beta_testers"))
            .build();

    // Register the user if not already seen
    client.identify(ldUser);

    LDMarkerFactory.LDMarker ldMarker = markerFactory.create("turbomarker", ldUser);

    logger.info(ldMarker, "Hello world, I am info");
    logger.debug(ldMarker, "Hello world, I am debug");

    ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
    assertThat(appender.list.size()).isEqualTo(2);

    appender.list.clear();
  }

  @Test
  @DisplayName("Non Matching Marker")
  public void testNonMatchingUserMarker() {
    LDClientInterface client = Mockito.mock(LDClientInterface.class);
    when(client.boolVariation(anyString(), any(), anyBoolean())).thenReturn(false);

    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger =
        loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    LDMarkerFactory markerFactory = new LDMarkerFactory(client);
    LDUser ldUser = new LDUser.Builder("NON_MATCHING").firstName("Not").lastName("Beta").build();

    // Register the user if not already seen
    client.identify(ldUser);

    LDMarkerFactory.LDMarker ldMarker = markerFactory.create("turbomarker", ldUser);
    logger.info(ldMarker, "Hello world, I am info");
    logger.debug(ldMarker, "Hello world, I am debug");

    ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
    assertThat(appender.list.size()).isEqualTo(0);

    appender.list.clear();
  }
}
