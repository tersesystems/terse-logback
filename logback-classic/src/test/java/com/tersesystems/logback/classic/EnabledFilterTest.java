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
package com.tersesystems.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.core.EnabledFilter;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnabledFilterTest {
    @Test
    public void testFilterFalse() {
        EnabledFilter enabledFilter = new EnabledFilter();
        enabledFilter.setEnabled(false);
        enabledFilter.start();

        ILoggingEvent loggingEvent = new LoggingEventVO();

        Assertions.assertThat(enabledFilter.decide(loggingEvent)).isEqualTo(FilterReply.DENY);
    }

    @Test
    public void testFilterTrue() {
        EnabledFilter enabledFilter = new EnabledFilter();
        enabledFilter.setEnabled(true);
        enabledFilter.start();

        ILoggingEvent loggingEvent = new LoggingEventVO();

        Assertions.assertThat(enabledFilter.decide(loggingEvent)).isEqualTo(FilterReply.NEUTRAL);
    }
}
