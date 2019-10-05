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
package com.tersesystems.logback.classic;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TerseHighlightConverterTest {

  @Test
  public void testHighlighter() {
    TerseHighlightConverter converter = new TerseHighlightConverter();
    LoggerContext context = new LoggerContext();
    converter.setContext(context);
    Map<String, String> properties = new HashMap<>();
    properties.put("info", "red");
    context.putObject(TerseHighlightConverter.HIGHLIGHT_CTX_KEY, properties);
    converter.start();
    LoggingEvent infoEvent =
        new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", null, null);
    String actual = converter.convert(infoEvent);

    assertThat(actual).contains(TerseHighlightConverter.Color.valueOf("RED").code);
  }
}
