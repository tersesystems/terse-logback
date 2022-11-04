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
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import java.util.Iterator;

/** This class selects an appender by the appender key. */
public class SelectAppender<E> extends AppenderBase<E> implements AppenderAttachable<E> {

  private AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

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
    if (isStarted()) {
      detachAndStopAllAppenders();
    }
    super.stop();
  }

  @Override
  public boolean isStarted() {
    return super.isStarted();
  }

  @Override
  protected void append(E eventObject) {
    Appender<E> appender = aai.getAppender(appenderKey);
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
  public void addAppender(Appender<E> newAppender) {
    aai.addAppender(newAppender);
  }

  @Override
  public Iterator<Appender<E>> iteratorForAppenders() {
    return aai.iteratorForAppenders();
  }

  @Override
  public Appender<E> getAppender(String name) {
    return aai.getAppender(name);
  }

  @Override
  public boolean isAttached(Appender<E> appender) {
    return aai.isAttached(appender);
  }

  @Override
  public void detachAndStopAllAppenders() {
    aai.detachAndStopAllAppenders();
  }

  @Override
  public boolean detachAppender(Appender<E> appender) {
    return aai.detachAppender(appender);
  }

  @Override
  public boolean detachAppender(String name) {
    return aai.detachAppender(name);
  }
}
