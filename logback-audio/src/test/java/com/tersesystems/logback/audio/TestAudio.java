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
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestAudio {

    @Test
    public void testAudio() {
        URL bellOgg = getClass().getResource("/bark.ogg");
        SimplePlayer.fromURL(bellOgg).play();
        SimplePlayer.fromURL(bellOgg).play();
    }

    @Test
    public void testMarkerWithURL() throws JoranException, InterruptedException {
        LoggerContext context = new LoggerContext();

        URL resource = getClass().getResource("/logback-with-marker-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        Logger logger = context.getLogger("some.random.Logger");

        URL audioURL = getClass().getResource("/bark.ogg");
        Marker marker = new AudioMarker(audioURL);
        logger.info(marker, "Bark!");
        Thread.sleep(1000);
    }

    @Test
    public void testMarkerWithPath() throws JoranException, InterruptedException {
        LoggerContext context = new LoggerContext();

        URL resource = getClass().getResource("/logback-with-marker-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        Logger logger = context.getLogger("some.random.Logger");

        String path = System.getProperty("user.dir");
        Path audioPath = Paths.get(path, "src", "test", "resources", "bark.ogg");
        Marker marker = new AudioMarker(audioPath);
        logger.warn(marker, "Bark!");
        Thread.sleep(1000);
    }

    @Test
    public void testLogger() throws JoranException, InterruptedException {
        LoggerContext context = new LoggerContext();

        URL resource = getClass().getResource("/logback-with-level-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        Logger logger = context.getLogger("some.random.Logger");

        for (int i = 0; i < 10; i++) {
            logger.trace("TRACE");
        }
        Thread.sleep(1000);

        for (int i = 0; i < 2; i++) {
            logger.debug("DEBUG");
        }
        Thread.sleep(1000);

        for (int i = 0; i < 2; i++) {
            logger.info("INFO");
        }
        Thread.sleep(1000);

        for (int i = 0; i < 2; i++) {
            logger.warn("WARN");
        }
        Thread.sleep(1000);

        logger.error("ERROR");
        Thread.sleep(10000);
    }


}
