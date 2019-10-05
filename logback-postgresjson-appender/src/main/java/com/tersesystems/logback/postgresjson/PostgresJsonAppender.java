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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.tersesystems.logback.classic.StartTime;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.Instant;
import java.util.Optional;
import org.postgresql.util.PGobject;

public class PostgresJsonAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private Encoder<ILoggingEvent> encoder;

  private HikariDataSource dataSource;

  private String sqlStatement;
  private String url;
  private String username;
  private String password;
  private int maxPoolSize = 2;

  public void setUrl(String url) {
    this.url = url;
  }

  public void setEncoder(Encoder<ILoggingEvent> encoder) {
    this.encoder = encoder;
  }

  public void setSqlStatement(String sqlStatement) {
    this.sqlStatement = sqlStatement;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setMaxPoolSize(int maxPoolSize) {
    this.maxPoolSize = maxPoolSize;
  }

  @Override
  public void start() {
    try {
      if (dataSource == null) {
        dataSource = createDataSource(url, username, password);
        // Check that the statement is accurate, no point in caching statement ref.
        // https://github.com/brettwooldridge/HikariCP#statement-cache
        dataSource.getConnection().prepareStatement(sqlStatement);
      }
      super.start();
    } catch (Exception e) {
      addError("Cannot configure database connection", e);
    }
  }

  @Override
  public void stop() {
    closeConnection();
    super.stop();
  }

  private void closeConnection() {
    if (dataSource != null) {
      try {
        dataSource.close();
      } catch (Exception e) {
        addError("Exception closing datasource", e);
      }
    }
  }

  private HikariDataSource createDataSource(String url, String username, String password) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    config.setPoolName("postgres-appender-hikari-pool");
    config.setMaximumPoolSize(maxPoolSize);
    return new HikariDataSource(config);
  }

  @Override
  protected void append(ILoggingEvent event) {
    try {
      byte[] encode = encoder.encode(event);
      Connection conn = dataSource.getConnection();
      PreparedStatement statement = conn.prepareStatement(sqlStatement);
      try {
        long eventMillis = event.getTimeStamp();
        statement.setTimestamp(1, new java.sql.Timestamp(eventMillis));
        statement.setLong(2, eventMillis);

        Optional<Long> startTime =
            StartTime.fromOptional(context, event).map(Instant::toEpochMilli);
        if (startTime.isPresent()) {
          statement.setLong(3, startTime.get());
        } else {
          statement.setNull(3, Types.BIGINT);
        }

        Level level = event.getLevel();
        statement.setInt(4, level.toInt());
        statement.setString(5, level.toString());

        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(new String(encode, StandardCharsets.UTF_8));
        statement.setObject(6, jsonObject);

        int results = statement.executeUpdate();
        addInfo("Inserted a row");
      } finally {
        statement.close();
        conn.close();
      }
    } catch (Exception e) {
      addError("Cannot insert event", e);
    }
  }
}
