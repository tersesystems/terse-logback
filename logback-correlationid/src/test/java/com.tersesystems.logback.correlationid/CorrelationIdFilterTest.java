package com.tersesystems.logback.correlationid;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.logback.core.StreamUtils;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class CorrelationIdFilterTest {

  @Test
  public void testFilter() throws JoranException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-correlationid.xml");

    // Write something that never gets logged explicitly...
    Logger logger = loggerFactory.getLogger("com.example.Debug");
    String correlationId = "12345";
    CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create(correlationId);

    // should be logged because marker
    logger.info(correlationIdMarker, "info one");

    logger.info("info two"); // should not be logged

    // Everything below this point should be logged.
    MDC.put("correlationId", correlationId);
    logger.info("info three"); // should not be logged
    logger.info(correlationIdMarker, "info four");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(3);
  }

  LoggerContext createLoggerFactory(String resourceName) throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource(resourceName);
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
    Optional<Appender<ILoggingEvent>> maybeAppender =
        getFirstAppender(context.getLogger(Logger.ROOT_LOGGER_NAME));
    if (maybeAppender.isPresent()) {
      return (ListAppender<ILoggingEvent>) requireNonNull(maybeAppender.get());
    } else {
      throw new IllegalStateException("Cannot find appender");
    }
  }

  private Optional<Appender<ILoggingEvent>> getFirstAppender(Logger logger) {
    Stream<Appender<ILoggingEvent>> appenderStream =
        StreamUtils.fromIterator(logger.iteratorForAppenders());
    return appenderStream.findFirst();
  }
}
