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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.Marker;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class UserMarkerFactory implements ContextAwareTurboMatcher<ApplicationContext> {

    private final Set<String> userIdSet = new ConcurrentSkipListSet<>();

    public void addUserId(String userId) {
        userIdSet.add(userId);
    }

    public void clear() {
        userIdSet.clear();
    }

    public UserMarkerAware create(ApplicationContext applicationContext) {
        return new UserMarkerAware("userMarker", applicationContext, this);
    }

    @Override
    public boolean match(ContextAwareTurboMarker marker, ApplicationContext applicationContext, Marker rootMarker, Logger logger, Level level, Object[] params, Throwable t) {
        return userIdSet.contains(applicationContext.currentUserId());
    }
}
