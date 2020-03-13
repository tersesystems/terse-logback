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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Optional;

public class StartTimeConverter extends ClassicConverter {
  @Override
  public String convert(ILoggingEvent event) {
    Optional<String> optStartTime =
        StartTime.fromOptional(getContext(), event).map(st -> Long.toString(st.toEpochMilli()));
    return optStartTime.orElse(null);
  }
}
