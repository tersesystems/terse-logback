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
package com.tersesystems.logback.exceptionmapping.json;

import static com.tersesystems.logback.exceptionmapping.Constants.DEFAULT_MAPPINGS_KEY;
import static com.tersesystems.logback.exceptionmapping.Constants.REGISTRY_BAG;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.tersesystems.logback.exceptionmapping.ExceptionMappingRegistry;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class TypesafeConfigMappingsActionTest {

  private final JoranConfigurator jc = new JoranConfigurator();
  private final LoggerContext loggerContext = new LoggerContext();

  @Before
  public void setUp() {
    jc.setContext(loggerContext);
  }

  @Test
  public void testConfig() throws JoranException {
    jc.doConfigure(
        requireNonNull(
            this.getClass().getClassLoader().getResource("logback-with-exception-mapping.xml")));

    Map<String, ExceptionMappingRegistry> registryMap =
        (Map<String, ExceptionMappingRegistry>) loggerContext.getObject(REGISTRY_BAG);
    ExceptionMappingRegistry registry = registryMap.get(DEFAULT_MAPPINGS_KEY);
    assertThat(
            registry.contains("com.tersesystems.logback.exceptionmapping.json.MySpecialException"))
        .isTrue();
  }
}
