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

import com.tersesystems.logback.compress.CompressingEncoder;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.encoder.EchoEncoder;
import ch.qos.logback.core.encoder.Encoder;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;


public class CompressingEncoderTest {

    private AtomicInteger counter = new AtomicInteger();

    public LoggingEvent mkEvent() {
        LoggingEvent loggingEvent = new LoggingEvent();
        loggingEvent.setMessage("hello world " + counter.getAndIncrement());
        loggingEvent.setLoggerName("org.example.Logger");
        loggingEvent.setLevel(Level.INFO);
        return loggingEvent;
    }

    @Test
    public void testCompressingEncoder() throws CompressorException {
        CompressorStreamFactory factory = CompressorStreamFactory.getSingleton();
        String zstandard = CompressorStreamFactory.getZstandard();
        Encoder<LoggingEvent> echo = new EchoEncoder<>();
        CompressingEncoder<LoggingEvent> encoder = new CompressingEncoder<>(echo, zstandard, factory, 1024);
        byte[] bytes = encoder.encode(mkEvent());

        assertThat(bytes).isEmpty();
    }

    @Test
    public void testNonCompressingEncoder() throws CompressorException, IOException {
        CompressorStreamFactory factory = CompressorStreamFactory.getSingleton();
        String zstandard = CompressorStreamFactory.getZstandard();
        Encoder<LoggingEvent> echo = new EchoEncoder<>();
        CompressingEncoder<LoggingEvent> encoder = new CompressingEncoder<>(echo, zstandard, factory, 10240);

        Stream<byte[]> stream = Stream.iterate(new byte[0], first -> {
            byte[] second = encoder.encode(mkEvent());
            return join(first, second);
        });
        byte[] bytes = stream.filter(b -> b.length > 0).limit(1).findFirst().get();
        byte[] compressed = join(bytes, encoder.footerBytes());

        assertThat(compressed).isNotEmpty();
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        try (CompressorInputStream cis = factory.createCompressorInputStream(bis)) {
            byte[] uncompress = Utils.readAllBytes(cis);
            String s = new String(uncompress, UTF_8);
            assertThat(s).startsWith("[INFO] hello world 0");
        }
    }

    private byte[] join(byte[] first, byte[] second) {
        byte[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }


}
