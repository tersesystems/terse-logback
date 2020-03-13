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
package com.tersesystems.logback.bytebuddy;

import static org.assertj.core.api.Assertions.assertThat;

import com.typesafe.config.Config;
import org.junit.jupiter.api.Test;

public class AdviceConfigTest {

  @Test
  public void testConfig() throws Exception {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    Config config = LoggingInstrumentationAdvice.generateConfig(classLoader, false);
    AdviceConfig adviceConfig =
        LoggingInstrumentationAdvice.generateAdviceConfig(classLoader, config, false);
    assertThat(adviceConfig.classNames())
        .contains("com.tersesystems.logback.bytebuddy.ClassCalledByAgent");
  }
}
