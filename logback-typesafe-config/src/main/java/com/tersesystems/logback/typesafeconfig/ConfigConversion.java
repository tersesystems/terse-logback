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

import ch.qos.logback.core.spi.ContextAware;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface ConfigConversion extends ContextAware {

    default Map<String, String> configAsMap(Config levelsConfig) {
        Map<String, String> levelsMap = new HashMap<>();
        Set<Map.Entry<String, ConfigValue>> levelsEntrySet = levelsConfig.entrySet();
        for (Map.Entry<String, ConfigValue> entry : levelsEntrySet) {
            String name = entry.getKey();
            try {
                String levelFromConfig = entry.getValue().unwrapped().toString();
                levelsMap.put(name, levelFromConfig);
            } catch (ConfigException.Missing e) {
                addInfo("No custom setting found for " + name + " in config, ignoring");
            } catch (Exception e) {
                addError("Unexpected exception resolving " + name, e);
            }
        }
        return levelsMap;
    }

}
