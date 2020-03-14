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
import com.tersesystems.logback.classic.IContainerLoggingEvent;
import com.tersesystems.logback.core.DecoratingAppender;

public class UniqueIdComponentAppender
    extends DecoratingAppender<ILoggingEvent, IContainerLoggingEvent> {

  private IdGenerator idGenerator = new FlakeIdGenerator();

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }

  public void setIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  protected IContainerLoggingEvent decorateEvent(ILoggingEvent eventObject) {
    IContainerLoggingEvent containerEvent;
    if (eventObject instanceof IContainerLoggingEvent) {
      containerEvent = (IContainerLoggingEvent) eventObject;
    } else {
      containerEvent = new ContainerProxyLoggingEvent(eventObject);
    }
    String uniqueId = idGenerator.generateId();
    containerEvent.putComponent(UniqueIdProvider.class, () -> uniqueId);
    return containerEvent;
  }
}
