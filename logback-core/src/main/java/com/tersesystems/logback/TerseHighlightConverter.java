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
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import com.typesafe.config.Config;

import java.util.Map;

/**
 * Prints out a colored level using ANSI codes.  Jansi is included here for Windows.
 *
 * This is like %highlight but uses configured colors instead.
 *
 * https://logback.qos.ch/manual/layouts.html#customConversionSpecifier
 */
public class TerseHighlightConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    public static final String HIGHLIGHT_CTX_KEY = "highlight";

    enum Color {
        BLACK(ANSIConstants.BLACK_FG),
        RED(ANSIConstants.RED_FG),
        GREEN(ANSIConstants.GREEN_FG),
        YELLOW(ANSIConstants.YELLOW_FG),
        BLUE(ANSIConstants.BLUE_FG),
        MAGENTA(ANSIConstants.MAGENTA_FG),
        CYAN(ANSIConstants.CYAN_FG),
        WHITE(ANSIConstants.WHITE_FG);

        final String code;
        Color(String code) {
            this.code = code;
        }
    }

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Map<String, String> config = (Map<String, String>) getContext().getObject(HIGHLIGHT_CTX_KEY);
        if (config == null) {
            addWarn("No map found in context with key " + HIGHLIGHT_CTX_KEY);
            return Color.BLACK.code;
        }

        Level level = event.getLevel();
        String levelColor = config.get(level.levelStr.toLowerCase()).toUpperCase();
        return Color.valueOf(levelColor).code;
    }
}


