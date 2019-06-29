package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionMessageConverterTest {

    @Test
    public void testNoException() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", null, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).contains("");
    }

    @Test
    public void testSingleMessage() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        RuntimeException ex = new RuntimeException("Hello world");

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", ex, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).isEqualTo(" [Hello world]");
    }

    @Test
    public void testNestedMessages() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        converter.setOptionList(Collections.singletonList("4"));
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        RuntimeException one = new RuntimeException("One");
        RuntimeException two = new RuntimeException("Two", one);
        RuntimeException three = new RuntimeException("Three", two);
        RuntimeException four = new RuntimeException("Four", three);

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", four, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).isEqualTo(" [Four > Three > Two > One]");
    }

    @Test
    public void testNestedMessagesWithCutOff() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        converter.setOptionList(Collections.singletonList("2"));
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        RuntimeException one = new RuntimeException("One");
        RuntimeException two = new RuntimeException("Two", one);
        RuntimeException three = new RuntimeException("Three", two);
        RuntimeException four = new RuntimeException("Four", three);

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", four, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).isEqualTo(" [Four > Three]");
    }

    @Test
    public void testNestedMessagesSeperator() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        converter.setOptionList(Arrays.asList("4", " [", " ! "));
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        RuntimeException one = new RuntimeException("One");
        RuntimeException two = new RuntimeException("Two", one);
        RuntimeException three = new RuntimeException("Three", two);
        RuntimeException four = new RuntimeException("Four", three);

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", four, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).isEqualTo(" [Four ! Three ! Two ! One]");
    }

    @Test
    public void testCustomPrefixSuffix() {
        ExceptionMessageConverter converter = new ExceptionMessageConverter();
        converter.setOptionList(Arrays.asList("4", "<", "|", ">"));
        LoggerContext context = new LoggerContext();
        converter.setContext(context);
        converter.start();

        RuntimeException one = new RuntimeException("One");
        RuntimeException two = new RuntimeException("Two", one);
        RuntimeException three = new RuntimeException("Three", two);
        RuntimeException four = new RuntimeException("Four", three);

        LoggingEvent infoEvent = new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", four, null);
        String actual = converter.convert(infoEvent);

        assertThat(actual).isEqualTo("<Four|Three|Two|One>");
    }
}
