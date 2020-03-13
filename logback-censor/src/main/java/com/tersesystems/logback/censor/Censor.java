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
package com.tersesystems.logback.censor;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Censor extends ContextAware, LifeCycle {

  /** Get the name of this appender. The name uniquely identifies the appender. */
  String getName();

  CharSequence censorText(CharSequence input);

  /**
   * Set the name of this appender. The name is used by other components to identify this appender.
   */
  void setName(String name);
}
