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
package com.tersesystems.logback.ext;

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

    Logger asLogger();

    class Trace implements LoggerStatement {
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

        @Override
        public Logger asLogger() {
            return new NoopLogger() {
                @Override
                public String getName() {
                    return logger.getName();
                }

                @Override
                public boolean isTraceEnabled() {
                    return logger.isTraceEnabled();
                }

                @Override
                public void trace(String msg) {
                    logger.trace(msg);
                }

                @Override
                public void trace(String format, Object arg) {
                    logger.trace(format, arg);
                }

                @Override
                public void trace(String format, Object arg1, Object arg2) {
                    logger.trace(format, arg1, arg2);
                }

                @Override
                public void trace(String format, Object... arguments) {
                    logger.trace(format, arguments);
                }

                @Override
                public void trace(String msg, Throwable t) {
                    logger.trace(msg, t);
                }

                @Override
                public boolean isTraceEnabled(Marker marker) {
                    return logger.isTraceEnabled(marker);
                }

                @Override
                public void trace(Marker marker, String msg) {
                    logger.trace(marker, msg);
                }

                @Override
                public void trace(Marker marker, String format, Object arg) {
                    logger.trace(marker, format, arg);
                }

                @Override
                public void trace(Marker marker, String format, Object arg1, Object arg2) {
                    logger.trace(marker, format, arg1, arg2);
                }

                @Override
                public void trace(Marker marker, String format, Object... argArray) {
                    logger.trace(marker, format, argArray);
                }

                @Override
                public void trace(Marker marker, String msg, Throwable t) {
                    logger.trace(marker, msg, t);
                }
            };
        }
    }

    class Debug implements LoggerStatement {
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

        @Override
        public Logger asLogger() {
            return new NoopLogger() {
                @Override
                public String getName() {
                    return logger.getName();
                }

                @Override
                public boolean isDebugEnabled() {
                    return logger.isDebugEnabled();
                }

                @Override
                public void debug(String msg) {
                    logger.debug(msg);
                }

                @Override
                public void debug(String format, Object arg) {
                    logger.debug(format, arg);
                }

                @Override
                public void debug(String format, Object arg1, Object arg2) {
                    logger.debug(format, arg1, arg2);
                }

                @Override
                public void debug(String format, Object... arguments) {
                    logger.debug(format, arguments);
                }

                @Override
                public void debug(String msg, Throwable t) {
                    logger.debug(msg, t);
                }

                @Override
                public boolean isDebugEnabled(Marker marker) {
                    return logger.isDebugEnabled(marker);
                }

                @Override
                public void debug(Marker marker, String msg) {
                    logger.debug(marker, msg);
                }

                @Override
                public void debug(Marker marker, String format, Object arg) {
                    logger.debug(marker, format, arg);
                }

                @Override
                public void debug(Marker marker, String format, Object arg1, Object arg2) {
                    logger.debug(marker, format, arg1, arg2);
                }

                @Override
                public void debug(Marker marker, String format, Object... argArray) {
                    logger.debug(marker, format, argArray);
                }

                @Override
                public void debug(Marker marker, String msg, Throwable t) {
                    logger.debug(marker, msg, t);
                }
            };
        }
    }

    class Info implements LoggerStatement {
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

        @Override
        public Logger asLogger() {
            return new NoopLogger() {
                @Override
                public String getName() {
                    return logger.getName();
                }

                @Override
                public boolean isInfoEnabled() {
                    return logger.isInfoEnabled();
                }

                @Override
                public void info(String msg) {
                    logger.info(msg);
                }

                @Override
                public void info(String format, Object arg) {
                    logger.info(format, arg);
                }

                @Override
                public void info(String format, Object arg1, Object arg2) {
                    logger.info(format, arg1, arg2);
                }

                @Override
                public void info(String format, Object... arguments) {
                    logger.info(format, arguments);
                }

                @Override
                public void info(String msg, Throwable t) {
                    logger.info(msg, t);
                }

                @Override
                public boolean isInfoEnabled(Marker marker) {
                    return logger.isInfoEnabled(marker);
                }

                @Override
                public void info(Marker marker, String msg) {
                    logger.info(marker, msg);
                }

                @Override
                public void info(Marker marker, String format, Object arg) {
                    logger.info(marker, format, arg);
                }

                @Override
                public void info(Marker marker, String format, Object arg1, Object arg2) {
                    logger.info(marker, format, arg1, arg2);
                }

                @Override
                public void info(Marker marker, String format, Object... argArray) {
                    logger.info(marker, format, argArray);
                }

                @Override
                public void info(Marker marker, String msg, Throwable t) {
                    logger.info(marker, msg, t);
                }
            };
        }
    }

    class Warn implements LoggerStatement {
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

        @Override
        public Logger asLogger() {
            return new NoopLogger() {
                @Override
                public String getName() {
                    return logger.getName();
                }

                @Override
                public boolean isWarnEnabled() {
                    return logger.isWarnEnabled();
                }

                @Override
                public void warn(String msg) {
                    logger.warn(msg);
                }

                @Override
                public void warn(String format, Object arg) {
                    logger.warn(format, arg);
                }

                @Override
                public void warn(String format, Object arg1, Object arg2) {
                    logger.warn(format, arg1, arg2);
                }

                @Override
                public void warn(String format, Object... arguments) {
                    logger.warn(format, arguments);
                }

                @Override
                public void warn(String msg, Throwable t) {
                    logger.warn(msg, t);
                }

                @Override
                public boolean isWarnEnabled(Marker marker) {
                    return logger.isWarnEnabled(marker);
                }

                @Override
                public void warn(Marker marker, String msg) {
                    logger.warn(marker, msg);
                }

                @Override
                public void warn(Marker marker, String format, Object arg) {
                    logger.warn(marker, format, arg);
                }

                @Override
                public void warn(Marker marker, String format, Object arg1, Object arg2) {
                    logger.warn(marker, format, arg1, arg2);
                }

                @Override
                public void warn(Marker marker, String format, Object... argArray) {
                    logger.warn(marker, format, argArray);
                }

                @Override
                public void warn(Marker marker, String msg, Throwable t) {
                    logger.warn(marker, msg, t);
                }
            };
        }
    }

    class Error implements LoggerStatement {
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

        @Override
        public Logger asLogger() {
            return new NoopLogger() {
                @Override
                public String getName() {
                    return logger.getName();
                }

                @Override
                public boolean isErrorEnabled() {
                    return logger.isErrorEnabled();
                }

                @Override
                public void error(String msg) {
                    logger.error(msg);
                }

                @Override
                public void error(String format, Object arg) {
                    logger.error(format, arg);
                }

                @Override
                public void error(String format, Object arg1, Object arg2) {
                    logger.error(format, arg1, arg2);
                }

                @Override
                public void error(String format, Object... arguments) {
                    logger.error(format, arguments);
                }

                @Override
                public void error(String msg, Throwable t) {
                    logger.error(msg, t);
                }

                @Override
                public boolean isErrorEnabled(Marker marker) {
                    return logger.isErrorEnabled(marker);
                }

                @Override
                public void error(Marker marker, String msg) {
                    logger.error(marker, msg);
                }

                @Override
                public void error(Marker marker, String format, Object arg) {
                    logger.error(marker, format, arg);
                }

                @Override
                public void error(Marker marker, String format, Object arg1, Object arg2) {
                    logger.error(marker, format, arg1, arg2);
                }

                @Override
                public void error(Marker marker, String format, Object... argArray) {
                    logger.error(marker, format, argArray);
                }

                @Override
                public void error(Marker marker, String msg, Throwable t) {
                    logger.error(marker, msg, t);
                }
            };
        }
    }

}