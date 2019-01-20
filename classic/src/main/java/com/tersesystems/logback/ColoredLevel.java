package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import static ch.qos.logback.core.pattern.color.ANSIConstants.*;

/**
 * Prints out a colored level using ANSI codes.  Jansi is included here for Windows.
 */
public class ColoredLevel extends ClassicConverter {

    static final class Colors {
        final private static String SET_DEFAULT_COLOR = ESC_START + "0;" + DEFAULT_FG + ESC_END;

        static String red(String str) {
            return (ESC_START + "0;" + RED_FG + ESC_END + str + SET_DEFAULT_COLOR);
        }

        static String cyan(String str) {
            return (ESC_START + "0;" + CYAN_FG + ESC_END + str + SET_DEFAULT_COLOR);
        }

        static String blue(String str) {
            return (ESC_START + "0;" + BLUE_FG + ESC_END + str + SET_DEFAULT_COLOR);
        }

        static String white(String str) {
            return (ESC_START + "0;" + WHITE_FG + ESC_END + str + SET_DEFAULT_COLOR);
        }

        static String yellow(String str) {
            return (ESC_START + "0;" + YELLOW_FG + ESC_END + str + SET_DEFAULT_COLOR);
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        switch (event.getLevel().toInt()) {
            case Level.TRACE_INT:
                return "[" + Colors.blue(event.getLevel().toString()) + "]";
            case Level.DEBUG_INT:
                return "[" + Colors.cyan(event.getLevel().toString()) + "]";
            case Level.INFO_INT:
                return "[" + Colors.white(event.getLevel().toString()) + "]";
            case Level.WARN_INT:
                return "[" + Colors.yellow(event.getLevel().toString()) + "]";
            case Level.ERROR_INT:
                return "[" + Colors.red(event.getLevel().toString()) + "]";
            default:
                return event.getLevel().toString();
        }
    }
}
