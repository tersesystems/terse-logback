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
 * Sets the logger levels using a map with the levels key.
 */
public class SetLoggerLevelsAction extends Action {

    public static final String LEVELS_KEY = "levels";
    public String levelsKey = LEVELS_KEY;

    public String getLevelsKey() {
        return levelsKey;
    }

    public void setLevelsKey(String levelsKey) {
        this.levelsKey = levelsKey;
    }

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        doConfigure(ic);
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }

    @SuppressWarnings("unchecked")
    protected void doConfigure(InterpretationContext ic) {
        LoggerContext ctx = (LoggerContext) ic.getContext();
        Map<String, String> levelsMap = (Map<String, String>) ctx.getObject(levelsKey);
        if (levelsMap == null) {
            addWarn("No levels found in context, cannot set levels.");
            return;
        }

        for (Map.Entry<String, String> entry : levelsMap.entrySet()) {
            String name = entry.getKey();
            try {
                Logger logger = ctx.getLogger(name);
                String level = entry.getValue();
                new ChangeLogLevel().changeLogLevel(logger, level);
                addInfo("Setting level of " + name + " logger to " + level);
            } catch (Exception e) {
                addError("Unexpected exception resolving " + name, e);
            }
        }

    }
}
