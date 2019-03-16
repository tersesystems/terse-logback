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
package com.tersesystems.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import com.typesafe.config.Config;
import org.junit.Test;

import static com.tersesystems.logback.TypesafeConfigAction.LOGBACK_DEBUG_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

public class TypesafeConfigActionTest {

    @Test
    public void testConfigAction() {
        TypesafeConfigAction action = new TypesafeConfigAction();

        System.setProperty(LOGBACK_DEBUG_PROPERTY, "true");
        Context lc = new LoggerContext();
        Config config = action.generateConfig(this.getClass().getClassLoader());
        action.configureContextWithConfig(lc, config);

        String fooValue = lc.getProperty("foo");
        assertThat(fooValue).isEqualTo("bar");
    }
}
