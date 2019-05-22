package com.tersesystems.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectAppenderTest {

    @Test
    public void testWithTestEnvironment() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-select-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        context.putProperty("APPENDER_KEY", "test");
        configurator.setContext(context);
        configurator.doConfigure(resource);

        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        SelectAppender selectAppender = (SelectAppender) logger.getAppender("SELECT");
        CompositeAppender<ILoggingEvent> development = (CompositeAppender<ILoggingEvent>) selectAppender.getAppender("test");
        ListAppender<ILoggingEvent> listAppender = (ListAppender<ILoggingEvent>) development.getAppender("test-list");

        logger.info("hello world");
        assertThat(listAppender.list.get(0).getMessage()).isEqualTo("hello world");
    }

    @Test
    public void testWithDevelopmentEnvironment() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-select-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        context.putProperty("APPENDER_KEY", "development");
        configurator.setContext(context);
        configurator.doConfigure(resource);

        ch.qos.logback.classic.Logger logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        SelectAppender selectAppender = (SelectAppender) logger.getAppender("SELECT");
        CompositeAppender<ILoggingEvent> development = (CompositeAppender<ILoggingEvent>) selectAppender.getAppender("test");
        ListAppender<ILoggingEvent> listAppender = (ListAppender<ILoggingEvent>) development.getAppender("test-list");

        logger.info("hello world");
        assertThat(listAppender.list.size()).isEqualTo(0);
    }
}
