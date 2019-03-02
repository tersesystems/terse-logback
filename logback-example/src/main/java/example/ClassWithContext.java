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

import com.tersesystems.logback.context.logstash.LogstashContext;
import com.tersesystems.logback.context.logstash.LogstashLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ClassWithContext {

    static class ObliviousToContext {
        private final Logger logger;

        public ObliviousToContext(ILoggerFactory lf) {
            this.logger = lf.getLogger(this.getClass().getName());
        }

        public void doStuff() {
            logger.info("hello world!");
        }
    }

    public static void main(String[] args) {
        // You can create objects that are oblivious to context, and just use the base
        // logstash markers...
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashContext context = LogstashContext.create("correlationId", correlationId);
        LogstashLoggerFactory loggerFactory = LogstashLoggerFactory.create(context);
        ObliviousToContext obliviousToContext = new ObliviousToContext(loggerFactory);
        obliviousToContext.doStuff();

        // Or you can create your own context and futzs with it.
        // Here we create an AppContext / AppLogger / AppLoggerFactory that lets us
        // set domain specific attributes on the context.
        AppContext appContext = AppContext.create().withCorrelationId(correlationId);
        AwareOfContext awareOfContext = new AwareOfContext(appContext);
        awareOfContext.doStuff();
    }

    private static class AwareOfContext {
        private final AppContext appContext;
        private final AppLogger logger;

        public AwareOfContext(AppContext appContext) {
            this.appContext = appContext;
            this.logger = AppLoggerFactory.create().getLogger(getClass()).withContext(appContext);
        }

        public void doStuff() {
            logger.info("My correlation id is {}", appContext.getCorrelationId().orElse("null"));
        }
    }
}
