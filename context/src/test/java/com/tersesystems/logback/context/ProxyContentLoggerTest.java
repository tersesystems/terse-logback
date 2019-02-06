package com.tersesystems.logback.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyContentLoggerTest {

    static class Foo {
        private final org.slf4j.Logger logger;

        Foo(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        public void doThat() {
            logger.info("hello world");
        }
    }

    @Test
    public void testContext() throws Exception {
        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        Context<LogstashMarker> context = LogstashContext.create("key1", "value1");
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger underlyingLogger = (ch.qos.logback.classic.Logger) loggerFactory.getLogger(Foo.class.getName());
        // add the appender to the logger
        underlyingLogger.addAppender(listAppender);
        Logger logger = new ProxyContextLogger<>(context, underlyingLogger);

        // call method under test
        Foo foo = new Foo(logger);
        foo.doThat();

        LogstashMarker contextMarker = Markers.appendEntries(Collections.singletonMap("key1", "value1"));
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getMarker)
                .containsExactly(Tuple.tuple("hello world", contextMarker));
    }
}
