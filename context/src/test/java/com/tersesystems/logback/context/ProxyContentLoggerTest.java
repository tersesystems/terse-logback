package com.tersesystems.logback.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.assertj.core.api.Condition;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Predicate;

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

        public void doThatWithMarker() {
            LogstashMarker marker = Markers.append("key2", "value2");
            logger.info(marker, "hello world with marker");
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


    @Test
    public void testContextWithMarkerr() throws Exception {
        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        Context<LogstashMarker> context1 = LogstashContext.create("key1", "value1");

        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger underlyingLogger = (ch.qos.logback.classic.Logger) loggerFactory.getLogger(Foo.class.getName());
        // add the appender to the logger
        underlyingLogger.addAppender(listAppender);
        Logger logger1 = new ProxyContextLogger<>(context1, underlyingLogger);

        // call method under test
        Foo foo = new Foo(logger1);
        foo.doThatWithMarker();

        LogstashMarker contextMarker = Markers.appendEntries(Collections.singletonMap("key1", "value1"));
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getMarker)
                .containsExactly(Tuple.tuple("hello world with marker", contextMarker));
    }

    //
    //    @Test
    //    public void testContextMerge() throws Exception {
    //        // create and start a ListAppender
    //        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    //        listAppender.start();
    //
    //        Context<LogstashMarker> context1 = LogstashContext.create("key1", "value1");
    //        Context<LogstashMarker> context2 = LogstashContext.create("key2", "value2");
    //
    //        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    //        ch.qos.logback.classic.Logger underlyingLogger = (ch.qos.logback.classic.Logger) loggerFactory.getLogger(Foo.class.getName());
    //        // add the appender to the logger
    //        underlyingLogger.addAppender(listAppender);
    //        Logger logger1 = new ProxyContextLogger<>(context1, underlyingLogger);
    //        Logger logger2 = new ProxyContextLogger<>(context2, logger1);
    //
    //        // call method under test
    //        Foo foo = new Foo(logger2);
    //        foo.doThat();
    //
    //        LogstashMarker parent = Markers.appendEntries(Collections.singletonMap("key1", "value1"));
    //        LogstashMarker child = Markers.appendEntries(Collections.singletonMap("key2", "value2"));
    //        Predicate<List<org.slf4j.Marker>> predicate = markerList -> {
    //            System.out.println(markerList);
    //            for (Marker marker : markerList) {
    //                List markerReferences = new ArrayList();
    //                marker.iterator().forEachRemaining(markerReferences::add);
    //                System.out.println("markerReferences = " + markerReferences);
    //
    //                boolean parentMatch = marker.equals(parent);
    //                boolean childMatch = marker.contains(child);
    //                return parentMatch && childMatch;
    //            }
    //            return false;
    //        };
    //        String description = "a %s foo";
    //        Condition condition = new Condition<>(predicate, description, "fairy tale");
    //        assertThat(listAppender.list).extracting(ILoggingEvent::getMarker).has(condition);
    //    }
}
