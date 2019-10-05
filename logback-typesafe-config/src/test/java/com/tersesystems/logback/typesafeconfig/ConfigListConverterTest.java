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
package com.tersesystems.logback.typesafeconfig;

import static com.tersesystems.logback.typesafeconfig.ConfigConstants.TYPESAFE_CONFIG_CTX_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Arrays;
import org.junit.Test;

public class ConfigListConverterTest {

  @Test
  public void testConversion() {
    LoggerContext context = new LoggerContext();
    ConfigListConverter configValueConverter = new ConfigListConverter();

    Config config = ConfigFactory.parseString("some.property.name=[one,two,three]");
    context.putObject(TYPESAFE_CONFIG_CTX_KEY, config);

    configValueConverter.setContext(context);
    configValueConverter.setOptionList(Arrays.asList("some.property.name", "1"));
    configValueConverter.start();

    String actual = configValueConverter.convert(null);

    assertThat(actual).isEqualTo("two");
  }
}
