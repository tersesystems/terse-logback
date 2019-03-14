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
package com.tersesystems.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Used to enable and disable appenders.
 */
public class EnabledFilter extends Filter<ILoggingEvent> {

    private boolean enabled;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (isStarted() && isEnabled()) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
