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

/**
 * A component container.
 *
 * <p>Entries are encouraged but not required to extend Component.
 */
public interface ComponentContainer {
  <T> void putComponent(Class<T> type, T instance);

  <T> T getComponent(Class<T> type);

  <T> boolean hasComponent(Class<T> type);
}
