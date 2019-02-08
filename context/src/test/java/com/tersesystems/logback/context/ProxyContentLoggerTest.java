package com.tersesystems.logback.context;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
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

        public void doThatWithMarker() {
            LogstashMarker marker = Markers.append("key2", "value2");
            logger.info(marker, "hello world with marker");
        }
    }

    @Test
    public void testContext() throws Exception {
        Logger logger = LoggerFactory.getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        Context<LogstashMarker> context = LogstashContext.create("context1", "value1");
        Logger proxyLogger = new ProxyContextLogger<>(context, logger);

        // call method under test
        Foo foo = new Foo(proxyLogger);
        foo.doThat();

        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\"}");
    }

    @Test
    public void testContextWithMarker() throws Exception {
        Logger logger = LoggerFactory.getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        Context<LogstashMarker> context1 = LogstashContext.create("context1", "value1");
        Logger proxyLogger = new ProxyContextLogger<>(context1, logger);

        // call method under test
        Foo foo = new Foo(proxyLogger);
        foo.doThatWithMarker();

        LogstashMarker contextMarker = Markers.appendEntries(Collections.singletonMap("context1", "value1"));
        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\",\"key2\":\"value2\"}");
    }

    private ListAppender<ILoggingEvent> addAppender(org.slf4j.Logger logger) {
        ch.qos.logback.classic.Logger underlyingLogger = (ch.qos.logback.classic.Logger) logger;
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        underlyingLogger.addAppender(listAppender);
        return listAppender;
    }

    @Test
    public void testContextMerge() throws Exception {
        Logger logger = LoggerFactory.getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        Context<LogstashMarker> context1 = LogstashContext.create("context1", "value1");
        Context<LogstashMarker> context2 = LogstashContext.create("context2", "value2");
        Logger logger1 = new ProxyContextLogger<>(context1, logger);
        Logger logger2 = new ProxyContextLogger<>(context2, logger1);

        // call method under test
        Foo foo = new Foo(logger2);
        foo.doThat();

        // Check that the output includes the content of contexts and markers.
        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\",\"context2\":\"value2\"}");
    }

    private String serializeMarker(ILoggingEvent event) throws IOException {
        LogstashMarkersJsonProvider provider = new LogstashMarkersJsonProvider();
        StringWriter writer = new StringWriter();
        JsonFactory factory = new MappingJsonFactory();
        JsonGenerator generator = factory.createGenerator(writer);
        generator.writeStartObject();
        assertThat(writer.toString()).isEqualTo("");
        provider.writeTo(generator, event);
        generator.writeEndObject();
        generator.flush();
        return writer.toString();
    }
}
