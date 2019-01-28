package com.tersesystems.logback;

import org.slf4j.Marker;
import org.slf4j.Logger;

/**
 * A statement that is tied to the particular level of the logger.
 */
public interface LoggerStatement {
    boolean isEnabled();

    void apply(String message);

    void apply(String format, Object arg);

    void apply(String format, Object arg1, Object arg2);

    void apply(String format, Object... arguments);

    void apply(String msg, Throwable t);

    boolean isEnabled(Marker marker);

    void apply(Marker marker, String msg);

    void apply(Marker marker, String format, Object arg);

    void apply(Marker marker, String format, Object arg1, Object arg2);

    void apply(Marker marker, String format, Object... argArray);

    void apply(Marker marker, String msg, Throwable t);


    public static class Trace implements LoggerStatement {
        private final Logger logger;

        public Trace(Logger logger) {
            this.logger = logger;
        }

        @Override
        public boolean isEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public void apply(String msg) {
            logger.trace(msg);
        }

        @Override
        public void apply(String format, Object arg) {
            logger.trace(format, arg);
        }

        @Override
        public void apply(String format, Object arg1, Object arg2) {
            logger.trace(format, arg1, arg2);
        }

        @Override
        public void apply(String format, Object... arguments) {
            logger.trace(format, arguments);
        }

        @Override
        public void apply(String msg, Throwable t) {
            logger.trace(msg, t);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void apply(Marker marker, String msg) {
            logger.trace(marker, msg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg) {
            logger.trace(marker, format, arg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg1, Object arg2) {
            logger.trace(marker, format, arg1, arg2);
        }

        @Override
        public void apply(Marker marker, String format, Object... argArray) {
            logger.trace(marker, format, argArray);
        }

        @Override
        public void apply(Marker marker, String msg, Throwable t) {
            logger.trace(marker, msg, t);
        }

    }

    public static class Debug implements LoggerStatement {
        private final Logger logger;

        public Debug(Logger logger) {
            this.logger = logger;
        }

        @Override
        public boolean isEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void apply(String msg) {
            logger.debug(msg);
        }

        @Override
        public void apply(String format, Object arg) {
            logger.debug(format, arg);
        }

        @Override
        public void apply(String format, Object arg1, Object arg2) {
            logger.debug(format, arg1, arg2);
        }

        @Override
        public void apply(String format, Object... arguments) {
            logger.debug(format, arguments);
        }

        @Override
        public void apply(String msg, Throwable t) {
            logger.debug(msg, t);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void apply(Marker marker, String msg) {
            logger.debug(marker, msg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg) {
            logger.debug(marker, format, arg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg1, Object arg2) {
            logger.debug(marker, format, arg1, arg2);
        }

        @Override
        public void apply(Marker marker, String format, Object... argArray) {
            logger.debug(marker, format, argArray);
        }

        @Override
        public void apply(Marker marker, String msg, Throwable t) {
            logger.debug(marker, msg, t);
        }

    }

    public static class Info implements LoggerStatement {
        private final Logger logger;

        public Info(Logger logger) {
            this.logger = logger;
        }

        @Override
        public boolean isEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public void apply(String msg) {
            logger.info(msg);
        }

        @Override
        public void apply(String format, Object arg) {
            logger.info(format, arg);
        }

        @Override
        public void apply(String format, Object arg1, Object arg2) {
            logger.info(format, arg1, arg2);
        }

        @Override
        public void apply(String format, Object... arguments) {
            logger.info(format, arguments);
        }

        @Override
        public void apply(String msg, Throwable t) {
            logger.info(msg, t);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isInfoEnabled(marker);
        }

        @Override
        public void apply(Marker marker, String msg) {
            logger.info(marker, msg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg) {
            logger.info(marker, format, arg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg1, Object arg2) {
            logger.info(marker, format, arg1, arg2);
        }

        @Override
        public void apply(Marker marker, String format, Object... argArray) {
            logger.info(marker, format, argArray);
        }

        @Override
        public void apply(Marker marker, String msg, Throwable t) {
            logger.info(marker, msg, t);
        }

    }

    public static class Warn implements LoggerStatement {
        private final Logger logger;

        public Warn(Logger logger) {
            this.logger = logger;
        }

        @Override
        public boolean isEnabled() {
            return logger.isWarnEnabled();
        }

        @Override
        public void apply(String msg) {
            logger.warn(msg);
        }

        @Override
        public void apply(String format, Object arg) {
            logger.warn(format, arg);
        }

        @Override
        public void apply(String format, Object arg1, Object arg2) {
            logger.warn(format, arg1, arg2);
        }

        @Override
        public void apply(String format, Object... arguments) {
            logger.warn(format, arguments);
        }

        @Override
        public void apply(String msg, Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void apply(Marker marker, String msg) {
            logger.warn(marker, msg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg) {
            logger.warn(marker, format, arg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg1, Object arg2) {
            logger.warn(marker, format, arg1, arg2);
        }

        @Override
        public void apply(Marker marker, String format, Object... argArray) {
            logger.warn(marker, format, argArray);
        }

        @Override
        public void apply(Marker marker, String msg, Throwable t) {
            logger.warn(marker, msg, t);
        }

    }

    public static class Error implements LoggerStatement {
        private final Logger logger;

        public Error(Logger logger) {
            this.logger = logger;
        }

        @Override
        public boolean isEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void apply(String msg) {
            logger.error(msg);
        }

        @Override
        public void apply(String format, Object arg) {
            logger.error(format, arg);
        }

        @Override
        public void apply(String format, Object arg1, Object arg2) {
            logger.error(format, arg1, arg2);
        }

        @Override
        public void apply(String format, Object... arguments) {
            logger.error(format, arguments);
        }

        @Override
        public void apply(String msg, Throwable t) {
            logger.error(msg, t);
        }

        @Override
        public boolean isEnabled(Marker marker) {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void apply(Marker marker, String msg) {
            logger.error(marker, msg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg) {
            logger.error(marker, format, arg);
        }

        @Override
        public void apply(Marker marker, String format, Object arg1, Object arg2) {
            logger.error(marker, format, arg1, arg2);
        }

        @Override
        public void apply(Marker marker, String format, Object... argArray) {
            logger.error(marker, format, argArray);
        }

        @Override
        public void apply(Marker marker, String msg, Throwable t) {
            logger.error(marker, msg, t);
        }

    }

}
