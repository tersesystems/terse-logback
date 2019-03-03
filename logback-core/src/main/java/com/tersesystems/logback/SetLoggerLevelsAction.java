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
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;
import org.xml.sax.Attributes;

import java.util.Map;
import java.util.Set;

/**
 * Sets the logger levels using typesafe config.
 */
public class SetLoggerLevelsAction extends Action {

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        doConfigure();
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }

    public void doConfigure() {
        LoggerContext context = (LoggerContext) getContext();
        Config rootConfig = (Config) context.getObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY);
        Config levelsConfig = rootConfig.getConfig(ConfigConstants.LEVELS_KEY);
        Set<Map.Entry<String, ConfigValue>> levelsEntrySet = levelsConfig.entrySet();
        for (Map.Entry<String, ConfigValue> entry : levelsEntrySet) {
            String name = entry.getKey();
            try {
                String levelFromConfig = entry.getValue().unwrapped().toString();
                Logger logger = context.getLogger(name);
                new ChangeLogLevel().changeLogLevel(logger, levelFromConfig);
                addInfo("Setting level of " + name + " logger to " + levelFromConfig);
            } catch (ConfigException.Missing e) {
                addInfo("No custom setting found for " + name + " in config, ignoring");
            } catch (Exception e) {
                addError("Unexpected exception resolving " + name, e);
            }
        }

    }
}
