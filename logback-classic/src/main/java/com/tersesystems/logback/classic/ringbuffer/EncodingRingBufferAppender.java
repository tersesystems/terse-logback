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
package com.tersesystems.logback.classic.ringbuffer;

import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * A cyclic buffer appender that takes an encoder and appends the encoded version of E.
 *
 * This is a good option if you want the encoded data (just plain bytes) and don't want to
 * hang on to possible references in the JVM (which may cause memory leaks depending on what is referenced
 * and the speed of the queue).  The downside is that you do lose the ability to do additional inspection
 * of events.
 *
 */
public class EncodingRingBufferAppender extends AbstractRingBufferAppender<ILoggingEvent, byte[]> {
    Encoder<ILoggingEvent> encoder;

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public void start() {
        if (this.encoder == null) {
            // Following from BasicConfigurator
            LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
            encoder.setContext(getContext());
            TTLLLayout layout = new TTLLLayout();
            layout.setContext(getContext());
            layout.start();
            encoder.setLayout(layout);
            encoder.start();
        }
        super.start();
    }

    public void stop() {
        this.encoder = null;
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        byte[] bytes = this.encoder.encode(eventObject);
        getRingBuffer().add(bytes);
    }
}
