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
package com.tersesystems.logback.compress;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class CompressingFileAppenderTest {

    @Test
    public void testAppender() throws JoranException, IOException, CompressorException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-zstd-encoder.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);

        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        for (int i = 0; i < 1000000; i++) {
            logger.error("This is an error message");
        }
        context.stop();

        Path filePath = Paths.get("encoded.zst");
        assertThat(filePath).exists();
        try {
            CompressorStreamFactory factory = CompressorStreamFactory.getSingleton();
            CompressorInputStream zin = factory.createCompressorInputStream("zstd", Files.newInputStream(filePath));

            byte[] decompress = Utils.readAllBytes(zin);
            assertThat(decompress).isNotEmpty();

            String actual = new String(decompress, UTF_8);
            assertThat(actual).startsWith("ERROR ROOT - This is an error message\n");
        } finally {
            Files.delete(filePath);
        }
    }

}
