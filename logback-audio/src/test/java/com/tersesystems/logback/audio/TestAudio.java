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

    // Can't keep a path steady with different starting directories..
    @Test
    public void testMarkerWithURLWithConverter() throws JoranException, InterruptedException {
        LoggerContext context = new LoggerContext();

        URL resource = getClass().getResource("/logback-with-converter.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        Logger logger = context.getLogger("some.random.Logger");

        URL audioURL = getClass().getResource("/bark.ogg");
        Marker marker = new AudioMarker(audioURL);
        logger.info(marker, "Bark!");
        Thread.sleep(1000);
    }

    // Can't keep a path steady with different starting directories..
    //@Test
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

}
