package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

import static ch.qos.logback.core.pattern.color.ANSIConstants.*;

/**
 * Prints out a colored level using ANSI codes.  Jansi is included here for Windows.
 *
 * This is like %highlight from but uses playframework colors instead.
 *
 * https://logback.qos.ch/manual/layouts.html#customConversionSpecifier
 */
public class TerseHighlightConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return BOLD + RED_FG;
            case Level.WARN_INT:
                return YELLOW_FG;
            case Level.INFO_INT:
                return WHITE_FG;
            case Level.DEBUG_INT:
                return CYAN_FG;
            case Level.TRACE_INT:
                return BLUE_FG;
            default:
                return DEFAULT_FG;
        }

    }
}


