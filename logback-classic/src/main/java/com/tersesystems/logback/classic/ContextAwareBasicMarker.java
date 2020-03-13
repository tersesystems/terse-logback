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
package com.tersesystems.logback.classic;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.status.*;

/** Extend the marker interface so that we can make it context aware. */
public class ContextAwareBasicMarker extends TerseBasicMarker implements ContextAware {
  private int noContextWarning = 0;
  protected Context context;

  public ContextAwareBasicMarker(String name) {
    super(name);
  }

  public void setContext(Context context) {
    if (this.context == null) {
      this.context = context;
    } else if (this.context != context) {
      throw new IllegalStateException("Context has been already set");
    }
  }

  public Context getContext() {
    return this.context;
  }

  public StatusManager getStatusManager() {
    if (context == null) {
      return null;
    }
    return context.getStatusManager();
  }

  /**
   * The declared origin of status messages. By default 'this'. Derived classes may override this
   * method to declare other origin.
   *
   * @return the declared origin, by default 'this'
   */
  protected Object getDeclaredOrigin() {
    return this;
  }

  public void addStatus(Status status) {
    if (context == null) {
      if (noContextWarning++ == 0) {
        System.out.println("LOGBACK: No context given for " + this);
      }
      return;
    }
    StatusManager sm = context.getStatusManager();
    if (sm != null) {
      sm.add(status);
    }
  }

  public void addInfo(String msg) {
    addStatus(new InfoStatus(msg, getDeclaredOrigin()));
  }

  public void addInfo(String msg, Throwable ex) {
    addStatus(new InfoStatus(msg, getDeclaredOrigin(), ex));
  }

  public void addWarn(String msg) {
    addStatus(new WarnStatus(msg, getDeclaredOrigin()));
  }

  public void addWarn(String msg, Throwable ex) {
    addStatus(new WarnStatus(msg, getDeclaredOrigin(), ex));
  }

  public void addError(String msg) {
    addStatus(new ErrorStatus(msg, getDeclaredOrigin()));
  }

  public void addError(String msg, Throwable ex) {
    addStatus(new ErrorStatus(msg, getDeclaredOrigin(), ex));
  }
}
