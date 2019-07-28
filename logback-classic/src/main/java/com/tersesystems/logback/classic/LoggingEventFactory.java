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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.slf4j.Marker;

import static ch.qos.logback.classic.Logger.FQCN;

public class LoggingEventFactory implements ILoggingEventFactory<ILoggingEvent> {
    public ILoggingEvent create(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        LoggingEvent le = new LoggingEvent(FQCN, logger, level, msg, t, params);
        le.setMarker(marker);
        return le;
    }
}
