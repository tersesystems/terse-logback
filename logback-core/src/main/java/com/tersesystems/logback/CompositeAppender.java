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

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;

/**
 * This appender creates a composite of the underlying appenders but does not add or change any functionality
 * of those appenders.
 *
 * It is very useful for referring to a list of appenders by a single name.
 *
 * @param <E> the event type, usually ILoggingEvent.
 */
public class CompositeAppender<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

    protected AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

    @Override
    protected void append(E eventObject) {
        aai.appendLoopOnAppenders(eventObject);
    }

    public void addAppender(Appender<E> newAppender) {
        addInfo("Attaching appender named [" + newAppender.getName() + "] to " + this.toString());
        aai.addAppender(newAppender);
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<E> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<E> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }

}
