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

public class UserAwareMarker extends ContextAwareTurboMarker<ApplicationContext, UserMarkerFactory> {
    public UserAwareMarker(String name, ApplicationContext applicationContext, UserMarkerFactory factory) {
        super(name, applicationContext, factory);
    }
}
