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
import ch.qos.logback.core.pattern.DynamicConverter;
import com.tersesystems.logback.core.ComponentContainer;

public class UniqueIdConverter extends DynamicConverter<ILoggingEvent> {
  @Override
  public String convert(ILoggingEvent event) {
    if (event instanceof ComponentContainer) {
      return ((ComponentContainer) event).getComponent(UniqueIdProvider.class).uniqueId();
    } else {
      return null;
    }
  }
}
