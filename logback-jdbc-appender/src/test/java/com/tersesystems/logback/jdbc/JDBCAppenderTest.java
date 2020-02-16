package com.tersesystems.logback.jdbc;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.net.URL;
import java.sql.*;
import org.junit.jupiter.api.Test;

public class JDBCAppenderTest {

  @Test
  public void testSimple() throws JoranException, SQLException {
    LoggerContext loggerFactory = createLoggerFactory("/logback-test.xml");

    // Write something that never gets logged explicitly...
    Logger logger = loggerFactory.getLogger(getClass());
    await().atMost(5, SECONDS).until(this::assertTablesExist);

    logger.info("info one");
    logger.info("info two");
    logger.info("info three");

    await().atMost(1, SECONDS).untilAsserted(() -> assertRowsEntered(3));
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

  public void assertRowsEntered(Integer expectedCount) throws SQLException {
    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:terse-logback", "sa", "")) {
      try (PreparedStatement p = conn.prepareStatement("select count(*) from events")) {
        assertThat(getCount(p)).isEqualTo(expectedCount);
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
        requireNonNull(context.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("JDBC"));
  }
}
