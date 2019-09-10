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
package com.tersesystems.logback.bytebuddy;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AdviceConfigTest {

    @Test
    public void testConfig() throws Exception {
        Config config = LoggingInstrumentationAdvice.generateConfig(ClassLoader.getSystemClassLoader(), false);
        LoggingInstrumentationAdviceConfig adviceConfig = LoggingInstrumentationAdvice.generateAdviceConfig(config);
        assertThat(adviceConfig.classNames()).contains("com.tersesystems.logback.bytebuddy.ClassCalledByAgent");
    }
}
