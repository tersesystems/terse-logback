package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.sift.AppenderTracker;
import com.tersesystems.logback.core.RingBuffer;
import com.tersesystems.logback.core.RingBufferAppender;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class LogbackUtils {

    public static LoggerContext getLoggerContext() {
        return ContextSelectorStaticBinder.getSingleton().getContextSelector().getLoggerContext();
    }

    public static Logger getRootLogger(LoggerContext loggerContext) {
        return requireNonNull(loggerContext).getLogger(Logger.ROOT_LOGGER_NAME);
    }

    public static Logger getRootLogger() {
        return getRootLogger(getLoggerContext());
    }

    public static Optional<Appender<ILoggingEvent>> getAppender(LoggerContext loggerContext, String appenderName) {
        Logger rootLogger = getRootLogger(loggerContext);
        return Optional.ofNullable(rootLogger.getAppender(requireNonNull(appenderName)));
    }

    public static Optional<Appender<ILoggingEvent>> getAppender(String appenderName) {
        return getAppender(getLoggerContext(), requireNonNull(appenderName));
    }

    public static Optional<SiftingAppender> getSiftingAppender(LoggerContext loggerContext, String siftAppenderName) {
        return getAppender(loggerContext, siftAppenderName).flatMap(a -> {
            if (a instanceof SiftingAppender) {
                return Optional.of((SiftingAppender) a);
            } else {
                return Optional.empty();
            }
        });
    }

    public static Optional<SiftingAppender> getSiftingAppender(String siftAppenderName) {
        return getAppender(getLoggerContext(), siftAppenderName).flatMap(a -> {
            if (a instanceof SiftingAppender) {
                return Optional.of((SiftingAppender) a);
            } else {
                return Optional.empty();
            }
        });
    }

    public static Optional<Appender<ILoggingEvent>> getAppenderByKey(SiftingAppender siftingAppender, String key) {
        AppenderTracker<ILoggingEvent> appenderTracker = siftingAppender.getAppenderTracker();
        return Optional.ofNullable(appenderTracker.find(key));
    }

    @SuppressWarnings("unchecked")
    public static <R> Optional<RingBuffer<R>> getRingBuffer(Appender<ILoggingEvent> appender) {
        if (appender instanceof RingBufferAppender<?, ?>) {
            RingBufferAppender<?, R> rs = (RingBufferAppender<?, R>) appender;
            return Optional.of(rs.getRingBuffer());
        } else {
            return Optional.empty();
        }
    }
}
