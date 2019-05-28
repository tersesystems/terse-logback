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
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;

/**
 * This class selects an appender by the appender key.
 */
public class SelectAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {

    private AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<ILoggingEvent>();

    private String appenderKey;

    @Override
    public void start() {
        if (appenderKey == null || appenderKey.isEmpty()) {
            addError("Null or empty appenderKey");
        } else {
            super.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean isStarted() {
        return super.isStarted();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        Appender<ILoggingEvent> appender = aai.getAppender(appenderKey);
        if (appender == null) {
            addError("No appender found for appenderKey " + appenderKey);
        } else {
            appender.doAppend(eventObject);
        }
    }

    public String getAppenderKey() {
        return appenderKey;
    }

    public void setAppenderKey(String appenderKey) {
        this.appenderKey = appenderKey;
    }

    @Override
    public void addAppender(Appender<ILoggingEvent> newAppender) {
        aai.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    @Override
    public Appender<ILoggingEvent> getAppender(String name) {
        return aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<ILoggingEvent> appender) {
        return aai.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<ILoggingEvent> appender) {
        return aai.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
}
