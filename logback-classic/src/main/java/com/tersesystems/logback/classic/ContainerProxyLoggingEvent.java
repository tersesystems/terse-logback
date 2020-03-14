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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** A logging event that implements a container and proxies another logging event. */
public class ContainerProxyLoggingEvent extends ProxyLoggingEvent
    implements IContainerLoggingEvent {
  private Map<Class<?>, Object> components = new HashMap<>();

  public ContainerProxyLoggingEvent(ILoggingEvent delegate) {
    super(delegate);
  }

  public <T> void putComponent(Class<T> type, T instance) {
    components.put(Objects.requireNonNull(type), instance);
  }

  public <T> T getComponent(Class<T> type) {
    return type.cast(components.get(type));
  }

  @Override
  public <T> boolean hasComponent(Class<T> type) {
    return components.containsKey(type);
  }
}
