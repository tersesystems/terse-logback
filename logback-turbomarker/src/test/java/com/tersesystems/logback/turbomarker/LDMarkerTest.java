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
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.launchdarkly.client.LDClient;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class LDMarkerTest {
    private static LDClientInterface client;

    @BeforeAll
    public static void setUp() {
        client = new LDClient("sdk-1a720ce0-d231-4ff7-8aef-5c54ad44da37");
    }

    @AfterAll
    public static void shutDown() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Matching Marker")
    public void testMatchingMarker() throws JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        LDMarkerFactory markerFactory = new LDMarkerFactory(client);
        LDUser ldUser = new LDUser.Builder("UNIQUE IDENTIFIER")
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
    public void testNonMatchingUserMarker() throws JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        LDMarkerFactory markerFactory = new LDMarkerFactory(client);
        LDUser ldUser = new LDUser.Builder("NON_MATCHING")
                .firstName("Not")
                .lastName("Beta")
                .build();

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
