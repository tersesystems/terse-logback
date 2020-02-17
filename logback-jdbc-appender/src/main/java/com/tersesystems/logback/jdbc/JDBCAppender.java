package com.tersesystems.logback.jdbc;

import static ch.qos.logback.core.CoreConstants.SAFE_JORAN_CONFIGURATION;
import static java.util.Objects.requireNonNull;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.tersesystems.logback.classic.StartTime;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import javax.sql.DataSource;

/**
 * This appender writes out to a single table through JDBC.
 *
 * <p>It uses HikariCP and a thread pool executor to set up the <a
 * href="https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing">appropriate thread pool
 * size</a>.
 *
 * <p>Note that despite using a thread pool sized to the database connection pool, you should always
 * use the JDBC appender behind an async appender of some sort, as you'll want to ensure that
 * there's a queue feeding into the workers if they're all busy.
 */
public class JDBCAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
  private final AtomicBoolean initialized = new AtomicBoolean(false);

  private Encoder<ILoggingEvent> encoder;
  private HikariDataSource dataSource;
  private Duration reaperDuration;

  private String driver;
  private String url;
  private String username;
  private String password;

  private String insertStatement;
  private String createStatements;
  private String reaperStatement;

  private String reaperSchedule;
  private String poolName = "jdbc-appender-pool-" + System.currentTimeMillis();
  private int poolSize = 2;

  private ExecutorService executorService;

  // Debug flag for checking that a row was inserted.
  protected boolean loggingInsert = false;
  private InsertConsumer insertConsumer;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Encoder<ILoggingEvent> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<ILoggingEvent> encoder) {
    this.encoder = encoder;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPoolSize(int poolSize) {
    this.poolSize = poolSize;
  }

  public String getPoolName() {
    return poolName;
  }

  public void setPoolName(String poolName) {
    this.poolName = poolName;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getReaperSchedule() {
    return reaperSchedule;
  }

  public void setReaperSchedule(String schedule) {
    this.reaperSchedule = reaperSchedule;
  }

  public String getReaperStatement() {
    return reaperStatement;
  }

  public void setReaperStatement(String reaperStatement) {
    this.reaperStatement = reaperStatement;
  }

  public String getCreateStatements() {
    return createStatements;
  }

  public void setCreateStatements(String createStatements) {
    this.createStatements = createStatements;
  }

  public String getInsertStatement() {
    return insertStatement;
  }

  public void setInsertStatement(String insertStatement) {
    this.insertStatement = insertStatement;
  }

  @Override
  public void start() {
    super.start();
    afterContextStarted();
    executorService = Executors.newFixedThreadPool(poolSize);
  }

  protected void afterContextStarted() {
    // We need initialization to happen AFTER logback is started, because otherwise
    // Hikari will start logging as it starts up, and cause NPE.
    // So we run this until it works and then chuck it.
    ScheduledExecutorService ses = context.getScheduledExecutorService();
    AtomicReference<ScheduledFuture<?>> self = new AtomicReference<>();
    ScheduledFuture<?> future =
        ses.scheduleAtFixedRate(
            () -> {
              if (context.getObject(SAFE_JORAN_CONFIGURATION) != null) {
                initialize();
                if (initialized.get()) {
                  self.get().cancel(true);
                }
              }
            },
            5,
            5,
            TimeUnit.MILLISECONDS);
    self.set(future);
  }

  @Override
  public void stop() {
    closeConnection();
    if (executorService != null) {
      try {
        executorService.awaitTermination(1, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    insertConsumer = null;
    initialized.set(false);
    super.stop();
  }

  // When the appender is starting, then Logback hasn't started up yet and so
  // we'll get very odd errors if the driver starts trying to log things itself.
  // Instead, we're going to register something that will start a datasource
  // when something comes through the pipeline.
  // Make this synchronized so that it blocks until we have the table created.
  protected synchronized void initialize() {
    if (!initialized.get()) {
      addInfo("initialize: ");
      try {
        dataSource = createDataSource(driver, url, username, password);
        checkConnection();

        // Initialize with DDL
        // XXX should really check if the table exists already
        createTable();
        insertConsumer = new InsertConsumer(dataSource);
        scheduleReaper();
        initialized.set(true);
      } catch (Exception e) {
        addError("Cannot configure database connection", e);
      }
    }
  }

  protected void checkConnection() throws SQLException {
    if (dataSource != null) {
      try (Connection conn = dataSource.getConnection()) {
        addInfo("checkConnection: trying isValid with a 1000 msec timeout");
        if (conn.isValid(1000)) {
          addInfo("checkConnection: isValid returned true!");
        } else {
          addWarn("checkConnection: isValid returned false!");
        }
      }
    }
  }

  protected void createTable() {
    String createStatements = getCreateStatements();
    if (createStatements == null || createStatements.trim().isEmpty()) {
      return;
    }

    addInfo("createTable: " + createStatements);
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (Statement stmt = conn.createStatement()) {
          stmt.executeUpdate(createStatements);
        }
      }
    } catch (SQLException e) {
      addWarn("Cannot create table, assuming it exists already", e);
    }
  }

  protected void scheduleReaper() {
    String reaperSchedule = getReaperSchedule();
    if (reaperSchedule == null || reaperSchedule.trim().isEmpty() || reaperDuration != null) {
      return;
    }
    addInfo("scheduleReaper: " + reaperSchedule);

    String reaperStatement = getReaperStatement();
    if (reaperStatement == null || reaperStatement.trim().isEmpty()) {
      addError(
          "scheduleReaper: reaperSchedule exists, but there is no reaperStatement to execute!");
      return;
    }

    reaperDuration = Duration.parse(reaperSchedule);
    ScheduledExecutorService ses = context.getScheduledExecutorService();
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            this::reapOldEvents,
            reaperDuration.toMillis(),
            reaperDuration.toMillis(),
            TimeUnit.MILLISECONDS);
    context.addScheduledFuture(scheduledFuture);
  }

  protected void reapOldEvents() {
    addInfo("reapOldEvents: ");
    try {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement(getReaperStatement())) {
          int results = stmt.executeUpdate();
          addInfo(String.format("Reaped %d statements", results));
        }
      }
    } catch (SQLException e) {
      addWarn("Cannot create table, assuming it exists already", e);
    }
  }

  protected void closeConnection() {
    if (dataSource != null) {
      try {
        dataSource.close();
        dataSource = null;
      } catch (Exception e) {
        addError("Exception closing datasource", e);
      }
    }
  }

  protected HikariDataSource createDataSource(
      String driver, String url, String username, String password) {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(Objects.requireNonNull(driver));
    config.setJdbcUrl(requireNonNull(url));
    config.setUsername(username);
    config.setPassword(password);
    config.setPoolName(poolName);
    config.setAutoCommit(true); // always use autocommit mode here.
    config.setMinimumIdle(poolSize);
    config.setMaximumPoolSize(poolSize);
    Properties props = new Properties();
    // props.put("dataSource.logWriter", new PrintWriter(System.out));
    config.setDataSourceProperties(props);
    return new HikariDataSource(config);
  }

  @Override
  protected void append(ILoggingEvent event) {
    executorService.submit(() -> insertConsumer.accept(event));
  }

  class InsertConsumer implements Consumer<ILoggingEvent> {
    private final DataSource dataSource;

    InsertConsumer(DataSource dataSource) {
      this.dataSource = requireNonNull(dataSource);
    }

    public void accept(ILoggingEvent event) {
      try (Connection conn = this.dataSource.getConnection()) {
        String insertStatement = requireNonNull(getInsertStatement());
        try (PreparedStatement statement = conn.prepareStatement(insertStatement)) {
          LongAdder adder = new LongAdder();
          adder.increment();
          int result = insertStatement(event, adder, statement);
          if (isLoggingInsert()) {
            String msg = String.format("Inserted resulted in %d rows added", result);
            addInfo(msg);
          }
        }
      } catch (Exception e) {
        addError("Cannot insert event, please check you are using a valid encoder!", e);
      }
    }
  }

  protected int insertStatement(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    insertTimestamp(event, adder, statement);
    insertTimestampMillis(event, adder, statement);
    insertStartTime(event, adder, statement);
    insertIntLevel(event, adder, statement);
    insertStringLevel(event, adder, statement);
    insertEvent(event, adder, statement);

    insertAdditionalData(event, adder, statement);

    return statement.executeUpdate();
  }

  /**
   * An empty method for use by subclasses who want to add additional fields.
   *
   * <p>Make sure to call adder.increment()
   *
   * @param event logging event
   * @param adder adder
   * @param statement prepared statement
   */
  protected void insertAdditionalData(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    // do nothing
  }

  protected void insertEvent(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    statement.setBytes(adder.intValue(), encoder.encode(event));
    adder.increment();
  }

  protected void insertStringLevel(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    statement.setString(adder.intValue(), event.getLevel().toString());
    adder.increment();
  }

  protected void insertIntLevel(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    Level level = event.getLevel();
    statement.setInt(adder.intValue(), level.toInt());
    adder.increment();
  }

  protected void insertStartTime(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    Optional<Long> startTime = StartTime.fromOptional(context, event).map(Instant::toEpochMilli);
    if (startTime.isPresent()) {
      statement.setLong(adder.intValue(), startTime.get());
    } else {
      statement.setNull(adder.intValue(), Types.BIGINT);
    }
    adder.increment();
  }

  protected void insertTimestampMillis(
      ILoggingEvent event, LongAdder adder, PreparedStatement statement) throws SQLException {
    statement.setLong(adder.intValue(), event.getTimeStamp());
    adder.increment();
  }

  protected void insertTimestamp(ILoggingEvent event, LongAdder adder, PreparedStatement statement)
      throws SQLException {
    long eventMillis = event.getTimeStamp();
    statement.setTimestamp(adder.intValue(), new Timestamp(eventMillis));
    adder.increment();
  }

  public boolean isLoggingInsert() {
    return loggingInsert;
  }
}
