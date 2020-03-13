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
import com.tersesystems.logback.classic.ContainerProxyLoggingEvent;
import com.tersesystems.logback.core.ComponentContainer;
import com.tersesystems.logback.core.DecoratingAppender;

public class UniqueIdEventAppender extends DecoratingAppender<ILoggingEvent, ILoggingEvent> {

  private IdGenerator idGenerator = new FlakeIdGenerator();

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }

  public void setIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  protected ILoggingEvent decorateEvent(ILoggingEvent eventObject) {
    ComponentContainer c = null;
    if (eventObject instanceof ComponentContainer) {
      c = (ComponentContainer) eventObject;
    } else {
      ContainerProxyLoggingEvent newEvent = new ContainerProxyLoggingEvent(eventObject);
      c = newEvent;
      eventObject = newEvent;
    }
    String uniqueId = idGenerator.generateId();
    c.putComponent(UniqueIdProvider.class, () -> uniqueId);
    return eventObject;
  }
}
