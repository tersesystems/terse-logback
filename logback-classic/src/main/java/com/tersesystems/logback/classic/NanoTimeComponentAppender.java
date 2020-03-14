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

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.DecoratingAppender;

/** This appender adds a relative nanotime component to the logging event. */
public class NanoTimeComponentAppender
    extends DecoratingAppender<ILoggingEvent, IContainerLoggingEvent> {
  @Override
  protected IContainerLoggingEvent decorateEvent(ILoggingEvent eventObject) {
    IContainerLoggingEvent containerEvent;
    if (eventObject instanceof IContainerLoggingEvent) {
      containerEvent = (IContainerLoggingEvent) eventObject;
    } else {
      containerEvent = new ContainerProxyLoggingEvent(eventObject);
    }
    long nanoTime = System.nanoTime() - NanoTime.start;
    containerEvent.putComponent(NanoTimeSupplier.class, () -> nanoTime);
    return containerEvent;
  }
}
