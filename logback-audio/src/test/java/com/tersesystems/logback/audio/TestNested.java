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
package com.tersesystems.logback.audio;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.junit.Test;
import org.slf4j.Marker;

import java.net.URL;

public class TestNested {

    @Test
    public void testLogger() throws JoranException, InterruptedException {
        LoggerContext context = new LoggerContext();

        URL resource = getClass().getResource("/logback-with-nested-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        Logger logger = context.getLogger("some.random.Logger");

        for (int i = 0; i < 1; i++) {
            logger.trace("TRACE");
        }

        for (int i = 0; i < 2; i++) {
            logger.debug("DEBUG");
        }

        for (int i = 0; i < 2; i++) {
            logger.info("INFO");
        }

        for (int i = 0; i < 2; i++) {
            logger.warn("WARN");
        }

        logger.error("ERROR");
    }


}
