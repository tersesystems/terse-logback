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
package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.ILoggingEventFactory;
import com.tersesystems.logback.classic.LoggingEventFactory;
import org.slf4j.Marker;

/**
 * Dumps logging events if an event with a level meeting the threshold level is seen.
 */
public class ThresholdRingBufferTurboFilter extends TurboFilter implements RingBufferAware<LoggingEvent> {

    private int capacity = 100;
    private RingBuffer<LoggingEvent> ringBuffer;
    private ILoggingEventFactory<LoggingEvent> loggingEventFactory;
    private String logger = Logger.ROOT_LOGGER_NAME;

    private Level thresholdLevel = Level.ERROR;
    private Level recordLevel = Level.DEBUG;

    public void setRecordLevel(String recordLevel) {
        this.recordLevel = Level.toLevel(recordLevel);
    }

    public void setThresholdLevel(String thresholdLevel) {
        this.thresholdLevel = Level.toLevel(thresholdLevel);
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    @Override
    public void start() {
        if (recordLevel.isGreaterOrEqual(thresholdLevel)) {
            addError("Threshold is lower or equal to level!");
        }
        if (loggingEventFactory == null) {
            this.loggingEventFactory = new LoggingEventFactory();
        }
        if (this.logger == null) {
            addError("No logger name was specified");
        }
        ringBuffer = new RingBuffer<>(capacity);

        super.start();
    }

    public boolean isDumpTriggered(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        return level.isGreaterOrEqual(thresholdLevel) ;
    }

    public boolean isRecordable(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        if (level.isGreaterOrEqual(logger.getEffectiveLevel())) return false;
        if (level.isGreaterOrEqual(recordLevel)) return true;
        return isSelectedLogger(logger);
    }

    public void setLoggingEventFactory(ILoggingEventFactory<LoggingEvent> loggingEventFactory) {
        this.loggingEventFactory = loggingEventFactory;
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        if (isDumpTriggered(marker, logger, level, msg, params, t)) {
            dump(logger);
        } else if (isRecordable(marker, logger, level, msg, params, t)) {
            record(marker, logger, level, msg, params, t);
        }
        return FilterReply.NEUTRAL;
    }

    protected void dump(Logger logger) {
        for (ILoggingEvent iLoggingEvent : ringBuffer) {
            logger.callAppenders(iLoggingEvent);
        }
        ringBuffer.clear();
    }

    protected void record(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        LoggingEvent le = loggingEventFactory.create(marker, logger, level, msg, params, t);
        ringBuffer.append(le);
    }

    protected boolean isSelectedLogger(Logger logger) {
        return logger.getName().startsWith(this.logger);
    }

    @Override
    public RingBuffer<LoggingEvent> getRingBuffer() {
        return ringBuffer;
    }
}
