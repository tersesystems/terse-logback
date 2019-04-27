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
package example;

import com.tersesystems.logback.ext.*;
import com.tersesystems.logback.ext.predicate.PredicateConditionalLogger;
import com.tersesystems.logback.ext.proxy.ProxyConditionalLogger;
import com.tersesystems.logback.ext.proxy.ProxyLazyLogger;
import com.tersesystems.logback.ext.proxy.ProxyLogger;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithExtendedLoggers {

    private ClassWithExtendedLoggers() {
    }

    private final Logger logger = getLogger(getClass());

    // An implementation of a conditional logger that requires a predicate.
    // Predicates are very useful for rate-limiting / sampling / logging budgets
    static class MyPredicateLogger implements PredicateConditionalLogger {
        private final Predicate<Level> predicate;
        private final Logger logger;

        public MyPredicateLogger(Logger logger, Predicate<Level> predicate) {
            this.logger = logger;
            this.predicate = predicate;
        }

        @Override
        public Predicate<Level> predicate() {
            return predicate;
        }

        @Override
        public Logger logger() {
            return logger;
        }
    }

    // interfaces with default methods can be joined together as mixins
    static class EverythingLoggerImpl implements EverythingLogger, ProxyLazyLogger, ProxyConditionalLogger, ProxyLogger {
        private final Logger logger;

        public EverythingLoggerImpl(Logger logger) {
            this.logger = logger;
        }

        // ...but do require a public logger() method
        @Override
        public Logger logger() {
            return logger;
        }
    }

    // You can provide a public interface that hides the proxies and logger() and only exposes the API.
    interface EverythingLogger extends LazyLogger, ConditionalLogger, Logger {
    }

    private void doLazy() {
        // For a single interface, you can use SAM and cast a lambda to the default interface :-)
        final LazyLogger lazyLogger = (ProxyLazyLogger) () -> logger;

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // Can use an lazy info statement and a Consumer if that's easier...
        lazyLogger.info(stmt -> stmt.apply(context, "I use a consumer"));

        // Or you can use an optional logger statement:
        Optional<LoggerStatement> info = lazyLogger.info();

        // Or you can get the logging statement as a logger...
        info.map(stmt -> stmt.asLogger()).ifPresent(this::useLogger);
    }

    private void useLogger(Logger logger) {
        logger.info("hello world!");
    }

    private void doConditionalStuff() {
        // Set up predicate logger to only log if this is my machine:
        final ConditionalLogger conditionalLogger = new MyPredicateLogger(logger, this::isDevelopmentEnvironment);

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // Log only if the level is info and the above conditions are met AND it's not too large to log
        conditionalLogger.ifInfo(this::objectIsNotTooLargeToLog, stmt -> {
            // Log very large thing in here...
            stmt.apply(context, "log if INFO && isDevelopmentEnvironment() && objectIsNotTooLargeToLog()");
        });
    }

    private void doEverything() {
        // Logger that has conditional, lazy, and normal logger.
        final EverythingLogger everythingLogger = new EverythingLoggerImpl(logger);

        // Use lazy logger info
        everythingLogger.debug().ifPresent(debug -> debug.apply("Lazy logger statement " + new Date()));

        // Use conditional logger
        LoggerStatement debug = everythingLogger.ifDebug(() -> true).get();
        debug.apply("Conditional debug statement");

        // Use normal logging statement
        everythingLogger.debug("normal logging statement");
    }

    private Boolean objectIsNotTooLargeToLog() {
        return true; // object is not too big
    }

    private Boolean isDevelopmentEnvironment(Level level) {
        return "development".equals(System.getenv("APP_ENVIRONMENT"));
    }

    public static void main(String[] args) {
        ClassWithExtendedLoggers classWithExtendedLoggers = new ClassWithExtendedLoggers();
        classWithExtendedLoggers.doLazy();
        classWithExtendedLoggers.doConditionalStuff();
        classWithExtendedLoggers.doEverything();
    }
}
