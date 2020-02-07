package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.jdbc.JDBCAppender;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

/** Writes out log entry with a correlation id explicitly. */
public class CorrelationIdJdbcAppender extends JDBCAppender {
  private String mdcKey = "correlation_id";

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  protected CorrelationIdUtils utils;

  @Override
  public void start() {
    super.start();
    utils = new CorrelationIdUtils(mdcKey);
  }

  @Override
  protected void insertAdditionalData(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    insertCorrelationId(event, adder, statement);
  }

  private void insertCorrelationId(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    Optional<String> maybeCorrelationId = utils.get(event.getMarker());
    if (maybeCorrelationId.isPresent()) {
      statement.setString(adder.intValue(), maybeCorrelationId.get());
    } else {
      statement.setNull(adder.intValue(), Types.VARCHAR);
    }
    adder.increment();
  }
}
