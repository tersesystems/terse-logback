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
package com.tersesystems.logback.uniqueid;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.classic.ProxyLoggingEvent;

public class UniqueIdLoggingEvent extends ProxyLoggingEvent implements IUniqueIdLoggingEvent {

  private final String uniqueId;

  UniqueIdLoggingEvent(ILoggingEvent delegate, String uniqueId) {
    super(delegate);
    this.uniqueId = uniqueId;
  }

  @Override
  public String uniqueId() {
    return this.uniqueId;
  }
}
