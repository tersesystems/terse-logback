package com.tersesystems.logback.correlationid;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.jdbc.JDBCAppender;
import com.tersesystems.logback.uniqueid.IUniqueIdLoggingEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

/** Writes out log entry with a correlation id and event id explicitly. */
public class CorrelationIdJDBCAppender extends JDBCAppender {
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
    insertEventId(event, adder, statement);
  }

  private void insertEventId(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    if (event instanceof IUniqueIdLoggingEvent) {
      IUniqueIdLoggingEvent eventWithId = (IUniqueIdLoggingEvent) event;
      statement.setString(adder.intValue(), eventWithId.uniqueId());
    } else {
      statement.setNull(adder.intValue(), Types.VARCHAR);
    }
    adder.increment();
  }

  private void insertCorrelationId(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    Optional<String> maybeCorrelationId = utils.get(event.getMDCPropertyMap(), event.getMarker());
    if (maybeCorrelationId.isPresent()) {
      statement.setString(adder.intValue(), maybeCorrelationId.get());
    } else {
      statement.setNull(adder.intValue(), Types.VARCHAR);
    }
    adder.increment();
  }
}
