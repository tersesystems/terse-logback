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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TerseHighlightConverterTest {

    @Test
    public void testHighlighter() {
        TerseHighlightConverter converter = new TerseHighlightConverter();
        LoggerContext context = new LoggerContext();
        Config config = ConfigFactory.parseString("properties.highlight { info = red }");
        context.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);
        converter.setContext(context);
        converter.start();
        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", null, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).contains( TerseHighlightConverter.Color.valueOf("RED").code);
    }
}
