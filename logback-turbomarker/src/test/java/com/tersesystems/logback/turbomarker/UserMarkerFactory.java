/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.turbomarker;

import ch.qos.logback.core.spi.FilterReply;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class UserMarkerFactory {

  private final Set<String> userIdSet = new ConcurrentSkipListSet<>();

  private final ContextDecider<ApplicationContext> decider =
      context ->
          userIdSet.contains(context.currentUserId()) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;

  public void addUserId(String userId) {
    userIdSet.add(userId);
  }

  public void clear() {
    userIdSet.clear();
  }

  public UserMarker create(ApplicationContext applicationContext) {
    return new UserMarker("userMarker", applicationContext, decider);
  }
}
