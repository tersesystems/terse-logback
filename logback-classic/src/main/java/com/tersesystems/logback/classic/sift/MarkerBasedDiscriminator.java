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
package com.tersesystems.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.sift.DefaultDiscriminator;
import com.tersesystems.logback.core.StreamUtils;

import java.util.Optional;

/**
 * A discriminator that looks for a marker containing discriminating logic.
 *
 * @param <LoggingEventT>
 */
public class MarkerBasedDiscriminator<LoggingEventT extends ILoggingEvent> extends AbstractDiscriminator<LoggingEventT> {

    private String key = "key";
    private String defaultValue = DefaultDiscriminator.DEFAULT;

    @Override
    public String getDiscriminatingValue(ILoggingEvent loggingEvent) {
        Optional<DiscriminatingValue> optMarker = getDiscriminatorMarker(loggingEvent);
        return optMarker.map(m -> m.getDiscriminatingValue(loggingEvent)).orElse(getDefaultValue());
    }

    public Optional<DiscriminatingValue> getDiscriminatorMarker(ILoggingEvent loggingEvent) {
        return StreamUtils.fromMarker(context, loggingEvent.getMarker())
                    .filter(marker -> marker instanceof DiscriminatingValue)
                    .map(m -> (DiscriminatingValue) m).findFirst();
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
