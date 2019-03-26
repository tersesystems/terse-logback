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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.typesafe.config.Config;
import org.junit.Test;

import static com.tersesystems.logback.typesafeconfig.ConfigConstants.LEVELS_KEY;
import static com.tersesystems.logback.typesafeconfig.ConfigConstants.TYPESAFE_CONFIG_CTX_KEY;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TypesafeConfigActionTest {

    @Test
    public void testConfigWithDefault() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(loggerContext);
        jc.doConfigure(requireNonNull(this.getClass().getClassLoader().getResource("typesafeconfig/config-with-default.xml")));

        Object levels = loggerContext.getObject(LEVELS_KEY);
        assertThat(levels).isNotNull();

        Config config = (Config) loggerContext.getObject(TYPESAFE_CONFIG_CTX_KEY);
        assertThat(config).isNotNull();

        String exportedToContext = loggerContext.getProperty("foo");
        assertThat(exportedToContext).isNull();

        String exportedFoo = loggerContext.getProperty("exportedFoo");
        assertThat(exportedFoo).isEqualTo("bar");
    }

    @Test
    public void testConfigWithContext() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(loggerContext);
        jc.doConfigure(requireNonNull(this.getClass().getClassLoader().getResource("typesafeconfig/config-with-context.xml")));

        Object levels = loggerContext.getObject(LEVELS_KEY);
        assertThat(levels).isNotNull();

        Config config = (Config) loggerContext.getObject(TYPESAFE_CONFIG_CTX_KEY);
        assertThat(config).isNotNull();

        String foo = loggerContext.getProperty("foo");
        assertThat(foo).isEqualTo("bar");

        String exportedFoo = loggerContext.getProperty("exportedFoo");
        assertThat(exportedFoo).isEqualTo("bar");
    }

    @Test
    public void testConfigWithLocal() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(loggerContext);
        jc.doConfigure(requireNonNull(this.getClass().getClassLoader().getResource("typesafeconfig/config-with-local.xml")));

        Object levels = loggerContext.getObject(LEVELS_KEY);
        assertThat(levels).isNotNull();

        Config config = (Config) loggerContext.getObject(TYPESAFE_CONFIG_CTX_KEY);
        assertThat(config).isNotNull();

        String foo = loggerContext.getProperty("foo");
        assertThat(foo).isNull();

        String exportedFoo = loggerContext.getProperty("exportedFoo");
        assertThat(exportedFoo).isEqualTo("bar");

        Object contextObjectFoo = loggerContext.getObject("contextObjectFoo");
        assertThat(contextObjectFoo).isEqualTo("pathValue");
    }
}
