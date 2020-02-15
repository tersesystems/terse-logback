package com.tersesystems.logback.jdbc;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

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
    logger.info("info one");

    boolean resultsExist = false;
    try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "")) {
      try (PreparedStatement p = conn.prepareStatement("select ts, evt from events")) {
        try (ResultSet rs = p.executeQuery()) {
          while (rs.next()) {
            resultsExist = true;
            Timestamp ts = rs.getTimestamp("ts");
            String json = rs.getString("evt");

            assertThat(json).isNotNull();
          }
        }
      }
    }
    assertThat(resultsExist).isTrue();
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
