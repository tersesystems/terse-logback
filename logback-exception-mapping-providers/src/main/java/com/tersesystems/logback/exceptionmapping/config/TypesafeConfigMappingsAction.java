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
package com.tersesystems.logback.exceptionmapping.config;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import com.tersesystems.logback.exceptionmapping.ExceptionMappingRegistry;
import com.tersesystems.logback.typesafeconfig.ConfigConstants;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.xml.sax.Attributes;

public class TypesafeConfigMappingsAction extends Action {

  @SuppressWarnings("unchecked")
  private ExceptionMappingRegistry getRegistry(InterpretationContext ic) {
    Object obj = ic.peekObject();
    if (obj == null) {
      addError("Not in an exception registry");
      return null;
    }

    if (obj instanceof ExceptionMappingRegistry) {
      return (ExceptionMappingRegistry) obj;
    }
    addError("Parent type is not an exception mapping registry!");
    return null;
  }

  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes)
      throws ActionException {
    ExceptionMappingRegistry registry = getRegistry(ic);
    if (registry == null) {
      addError("Required exception registry is missing!");
      return;
    }

    Config config = getConfig(ic);
    if (config == null) {
      addError("Required typesafe config is missing!");
      return;
    }
    String mappingsPath = attributes.getValue("path");
    if (mappingsPath == null) {
      addError("Required attribute 'path' is missing!");
      return;
    }

    try {
      Map<String, List<String>> mappings = getMappingsFromConfig(config, mappingsPath);
      registry.register(mappings);
    } catch (ConfigException e) {
      addError("Could not resolve configuration using path " + mappingsPath, e);
    }
  }

  private Config getConfig(InterpretationContext ic) {
    Object obj = ic.getObjectMap().get(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY);
    if (obj == null) {
      return null;
    }
    if (obj instanceof Config) {
      return (Config) obj;
    }
    addError("Type is not a Config!");
    return null;
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {}

  public Map<String, List<String>> getMappingsFromConfig(Config config, String mappingsPath) {
    Config mappingsConfig = config.getConfig(mappingsPath);
    return mappingsConfig.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                (Map.Entry<String, ConfigValue> e) -> mappingsConfig.getStringList(e.getKey())));
  }
}
