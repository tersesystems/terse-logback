package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import com.typesafe.config.Config;

import static ch.qos.logback.core.pattern.color.ANSIConstants.*;

/**
 * Prints out a colored level using ANSI codes.  Jansi is included here for Windows.
 *
 * This is like %highlight but uses configured colors instead.
 *
 * https://logback.qos.ch/manual/layouts.html#customConversionSpecifier
 */
public class TerseHighlightConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    public static final String PROPERTIES_HIGHLIGHT = "properties.highlight";

    enum Color {
        BLACK(ANSIConstants.BLACK_FG),
        RED(ANSIConstants.RED_FG),
        GREEN(ANSIConstants.GREEN_FG),
        YELLOW(ANSIConstants.YELLOW_FG),
        BLUE(ANSIConstants.BLUE_FG),
        MAGENTA(ANSIConstants.MAGENTA_FG),
        CYAN(ANSIConstants.CYAN_FG),
        WHITE(ANSIConstants.WHITE_FG);

        private final String code;
        Color(String code) {
            this.code = code;
        }
    }

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Config config = (Config) getContext().getObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY);
        Config highlightConfig = config.getConfig(PROPERTIES_HIGHLIGHT);

        Level level = event.getLevel();
        String levelColor = highlightConfig.getString(level.levelStr.toLowerCase()).toUpperCase();
        return Color.valueOf(levelColor).code;
    }
}


