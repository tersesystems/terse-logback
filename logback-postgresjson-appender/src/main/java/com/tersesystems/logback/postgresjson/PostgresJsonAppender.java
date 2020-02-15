/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.postgresjson;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.jdbc.JDBCAppender;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.LongAdder;
import org.postgresql.util.PGobject;

/** Extends the JDBC appender to write out Postgres JSON object. */
public class PostgresJsonAppender extends JDBCAppender {

  private String objectType = "json";

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  @Override
  public void start() {
    super.start();
    setDriver("org.postgresql.Driver");
  }

  @Override
  protected void insertEvent(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    PGobject jsonObject = new PGobject();
    jsonObject.setType(getObjectType());
    byte[] bytes = getEncoder().encode(event);
    jsonObject.setValue(new String(bytes, StandardCharsets.UTF_8));
    statement.setObject(adder.intValue(), jsonObject);
    adder.increment();
  }
}
