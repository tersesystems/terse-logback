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

package com.tersesystems.logback.ringbuffer;

import static com.tersesystems.logback.ringbuffer.RingBufferConstants.RINGBUFFER_BAG;

import ch.qos.logback.classic.LoggerContext;
import java.util.Map;

public final class RingBufferUtils {

  @SuppressWarnings("unchecked")
  static RingBufferContextAware getRingBuffer(LoggerContext loggerFactory, String name) {
    Map<String, RingBufferContextAware> ringBufferBag =
        (Map<String, RingBufferContextAware>) loggerFactory.getObject(RINGBUFFER_BAG);
    return ringBufferBag.get(name);
  }
}
