package com.tersesystems.logback.correlationid;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import com.tersesystems.logback.core.StreamUtils;
import com.tersesystems.logback.jdbc.JDBCAppender;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class CorrelationIdJdbcTest {

  @Test
  public void testJdbcAppender() throws JoranException, SQLException, InterruptedException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-correlationid-jdbc.xml");
    Logger logger = loggerFactory.getLogger("com.example.ExampleClass");

    String cid1 = "12345";
    String key = "correlationId";
    CorrelationIdMarker marker = CorrelationIdMarker.create(cid1);

    logger.info(marker, "info one");
    logger.info(marker, "info two");
    await().atMost(5, SECONDS).until(this::assertTablesExist);

    String cid2 = "32411";
    MDC.put(key, cid2);
    logger.info("info three");
    logger.info("info four");
    logger.info("info five");

    // Give the async appender time to flush
    await().atMost(1, SECONDS).untilAsserted(() -> assertRowsEntered(cid1, cid2));
  }

  private Boolean assertTablesExist() {
    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:terse-logback", "sa", "")) {
      try (PreparedStatement p = conn.prepareStatement("select count(*) from events")) {
        return p.execute();
      }
    } catch (SQLException e) {
      return false;
    }
  }

  public void assertRowsEntered(String cid1, String cid2) throws SQLException {
    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:terse-logback", "sa", "")) {
      try (PreparedStatement p =
          conn.prepareStatement("select count(*) from events where correlation_id = ?")) {
        p.setString(1, cid1);
        assertThat(getCount(p)).isEqualTo(2);
        p.setString(1, cid2);
        assertThat(getCount(p)).isEqualTo(3);
      }
    }
  }

  public int getCount(PreparedStatement p) throws SQLException {
    try (ResultSet rs = p.executeQuery()) {
      if (rs.next()) {
        return rs.getInt(1);
      } else {
        return 0;
      }
    }
  }

  LoggerContext createLoggerFactory(String resourceName) throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource(resourceName);
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  JDBCAppender getJDBCAppender(LoggerContext context) {
    return (JDBCAppender)
        requireNonNull(context.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("ASYNC_JDBC"));
  }

  private Optional<Appender<ILoggingEvent>> getFirstAppender(Logger logger) {
    Stream<Appender<ILoggingEvent>> appenderStream =
        StreamUtils.fromIterator(logger.iteratorForAppenders());
    return appenderStream.findFirst();
  }
}
