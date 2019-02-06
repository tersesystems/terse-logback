package com.tersesystems.logback.context;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyContentLoggerTest {

    @Test
    public void testContext() throws Exception {
        LoggerContext loggerContext = new LoggerContext();
        ListAppender<LoggingEvent> listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.start();
        loggerContext.start();

        Context<LogstashMarker> context = LogstashContext.create();
        ILoggerFactory proxyLogger = ProxyContextLoggerFactory.create(context, loggerContext);
        Logger logger = proxyLogger.getLogger("example.Class");
        logger.info("hello world!");

        assertThat(listAppender.list).isNotEmpty();
    }
}
