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
package com.tersesystems.logback.censor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class CensorActionTest {

    private JoranConfigurator jc = new JoranConfigurator();
    private LoggerContext loggerContext = new LoggerContext();

    @Before
    public void setUp() {
        jc.setContext(loggerContext);
    }

    @Test
    public void testFirstTest1() throws JoranException {
        jc.doConfigure(requireNonNull(this.getClass().getClassLoader().getResource("logback-test.xml")));

        ch.qos.logback.classic.Logger root = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        TestAppender test = (TestAppender) root.getAppender("TEST1");
        assertThat(test).isNotNull();
        byte[] bytes = test.getEncoder().encode(createLoggingEvent(root, "hunter1"));
        assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("[CENSORED BY CENSOR1]");
    }

    @Test
    public void testSecondTest1() throws JoranException {
        jc.doConfigure(requireNonNull(this.getClass().getClassLoader().getResource("logback-test.xml")));

        ch.qos.logback.classic.Logger root = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        TestAppender test = (TestAppender) root.getAppender("TEST2");
        assertThat(test).isNotNull();
        byte[] bytes = test.getEncoder().encode(createLoggingEvent(root, "hunter2"));
        assertThat(new String(bytes, StandardCharsets.UTF_8)).contains("[CENSORED BY CENSOR2]");
    }

    private LoggingEvent createLoggingEvent(ch.qos.logback.classic.Logger logger, String message) {
        return new LoggingEvent(this.getClass().getName(), logger, Level.DEBUG, message, null, null);
    }
}
