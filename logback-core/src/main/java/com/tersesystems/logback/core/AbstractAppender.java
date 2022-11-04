/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.core;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import java.util.Iterator;

/**
 * Provides abstract appender behavior with pre / post behavior.
 *
 * @param <E> the input type, usually ILoggingEvent
 */
public abstract class AbstractAppender<E> extends UnsynchronizedAppenderBase<E>
    implements AppenderAttachable<E> {

  protected AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

  protected abstract E appendEvent(E eventObject);

  @Override
  protected void append(E eventObject) {
    preAppend();
    aai.appendLoopOnAppenders(appendEvent(eventObject));
    postAppend();
  }

  protected void postAppend() {}

  protected void preAppend() {}

  public void addAppender(Appender<E> newAppender) {
    addInfo("Attaching appender named [" + newAppender.getName() + "] to " + this.toString());
    aai.addAppender(newAppender);
  }

  public Iterator<Appender<E>> iteratorForAppenders() {
    return aai.iteratorForAppenders();
  }

  public void stop() {
    super.stop();
    aai.detachAndStopAllAppenders();
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
