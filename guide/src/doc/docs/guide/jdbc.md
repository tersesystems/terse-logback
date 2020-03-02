# JDBC

There is a JDBC appender included which can be subclassed and extended as necessary in the `logback-jdbc-appender` module.  Using a database for logging can be a big help when you just want to get at the logs of the last 30 seconds from inside the application.  Because JDBC is both accessible and understandable, there's very little work required for querying.

Logback **does** have a native JDBC appender, but unfortunately it requires three tables and is not set up for easy subclassing.  This one is better.
 
This implementation assumes a single table, with a user defined extensible schema, and is set up with [HikariCP](https://github.com/brettwooldridge/HikariCP)  and a thread pool executor to serve JDBC with minimal blocking.  Note that you should **always** use a JDBC appender behind an `LoggingEventAsyncDisruptorAppender` and you should have an [appropriately sized connection pool](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing) for your database traffic.

Database timestamps record time with microsecond resolution, whereas millisecond resolution is commonplace for logging, so for convenience both the timestamp with time zone and the time since epoch are recorded.  For span information, the start time must also be recorded as TSE.  Likewise, the level is recorded as both a text string for visual reference, and a level value so that you can order and filter database queries.

Querying a database can be particuarly helpful when errors occur, because you can pull out all logs with a correlation id.  See the `logback-correlationid` module for an example.

### Logging using in-memory H2 Database

Using an in memory H2 database is a cheap and easy way to expose logs from inside the application without having to parse files.

```xml
<appender name="H2_JDBC" class="com.tersesystems.logback.jdbc.JDBCAppender">
    <driver>jdbc:h2:mem:logback</driver>
    <url>org.h2.Driver</url>
    <username>sa</username>
    <password></password>
    
    <createStatements>
      CREATE TABLE IF NOT EXISTS events (
         ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
         ts TIMESTAMP(9) WITH TIME ZONE NOT NULL,
         tse_ms numeric NOT NULL,
         start_ms numeric NULL,
         level_value int NOT NULL,
         level VARCHAR(7) NOT NULL,
         evt JSON NOT NULL
      );
    </createStatements>
    <insertStatement>insert into events(ts, tse_ms, start_ms, level_value, level, evt) values(?, ?, ?, ?, ?, ?)</insertStatement>
    <reaperStatement>delete from events where ts &lt; ?</reaperStatement>
    <reaperSchedule>PT30</reaperSchedule>

    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
    </encoder>
</appender>
```

### Logging using PostgresSQL

If you want something larger scale, you'll probably be using Postgres instead of H2.  You can log JSON to PostgreSQL, using the [built-in JSON datatype](https://www.postgresql.org/docs/current/functions-json.html).  Postgres uses a custom JDBC type of `PGObject`, so the `insertEvent` method must be subclassed.  This is what's in the `logback-postgresjson-appender` module:

```java
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
```

First, install PostgreSQL, create a database `logback`, a role `logback` and a password `logback` and add the following table:

```sql
CREATE TABLE logging_table (
   ID serial NOT NULL PRIMARY KEY,
   ts TIMESTAMPTZ(6) NOT NULL,
   tse_ms numeric NOT NULL,
   start_ms numeric NULL,
   level_value int NOT NULL,
   level VARCHAR(7) NOT NULL,
   evt jsonb NOT NULL
);
CREATE INDEX idxgin ON logging_table USING gin (evt);
```

Because logs are inherently time-series data, you can use the [timescaleDB postgresql extension](https://docs.timescale.com/latest/introduction) as described in [Store application logs in timescaleDB/postgres](https://www.komu.engineer/blogs/timescaledb/timescaledb-for-logs), but that's not required.

Then, add the following `logback.xml`:

```xml
<configuration>
    <!-- async appender needs a shutdown hook to make sure this clears -->
    <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>

    <conversionRule conversionWord="startTime" converterClass="com.tersesystems.logback.classic.StartTimeConverter" />

    <!-- SQL is blocking, so use an async lmax appender here -->
    <appender name="ASYNC_POSTGRES" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender class="com.tersesystems.logback.postgresjson.PostgresJsonAppender">
            <createStatements>
                CREATE TABLE IF NOT EXISTS logging_table (
                ID serial NOT NULL PRIMARY KEY,
                ts TIMESTAMPTZ(6) NOT NULL,
                tse_ms bigint NOT NULL,
                start_ms bigint NULL,
                level_value int NOT NULL,
                level VARCHAR(7) NOT NULL,
                evt jsonb NOT NULL
                );
                CREATE INDEX idxgin ON logging_table USING gin (evt);
           </createStatements>

           <!-- SQL statement takes a TIMESTAMP, LONG, INT, VARCHAR, PGObject -->
           <insertStatement>insert into logging_table(ts, tse_ms, start_ms, level_value, level, evt) values(?, ?, ?, ?, ?, ?)</insertStatement>

            <url>jdbc:postgresql://localhost:5432/logback</url>
            <username>logback</username>
            <password>logback</password>

            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <message/>
                    <loggerName/>
                    <threadName/>
                    <logLevel/>
                    <stackHash/>
                    <mdc/>
                    <logstashMarkers/>
                    <pattern>
                        <pattern>
                            { "start_ms": "#asLong{%startTime}" }
                        </pattern>
                    </pattern>
                    <arguments/>
                    <stackTrace>
                        <throwableConverter class="net.logstash.logback.stacktrace.ShortenedThrowableConverter">
                            <rootCauseFirst>true</rootCauseFirst>
                        </throwableConverter>
                    </stackTrace>
                </providers>
            </encoder>
        </appender>
    </appender>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="ASYNC_POSTGRES"/>
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

[Querying](http://clarkdave.net/2013/06/what-can-you-do-with-postgresql-and-json/) requires a little bit of extra syntax, using `evt->'myfield'` to select:

```sql
select 
  ts as end_date, 
  start_ms as epoch_start, 
  tse_ms as epoch_end, 
  evt->'trace.span_id' as span_id, 
  evt->'name' as name, 
  evt->'message' as message,  
  evt->'trace.parent_id' as parent,
  evt->'duration_ms' as duration_ms 
from logging_table where evt->'trace.trace_id' IS NOT NULL order by ts desc limit 5
```

If you have extra logs that you want to import into PostgreSQL, you can [use PSQL to do that](https://stackoverflow.com/questions/39224382/how-can-i-import-a-json-file-into-postgresql/57445995#57445995).

### Extending JDBC Appender with extra fields

The JDBC appender can be extended so you can add extra information to the table. 
  
In the `logback-correlationid` module, there's a `CorrelationIdJdbcAppender` that adds extra information into the event so you can query by the correlation id specifically, by using the `insertAdditionalData` hook:

```java
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
```

Then set up the table and add an index on the correlation id:

```sql
CREATE TABLE IF NOT EXISTS events (
   ID NUMERIC NOT NULL PRIMARY KEY AUTO_INCREMENT,
   ts TIMESTAMP(9) WITH TIME ZONE NOT NULL,
   tse_ms numeric NOT NULL,
   start_ms numeric NULL,
   level_value int NOT NULL,
   level VARCHAR(7) NOT NULL,
   evt JSON NOT NULL,
   correlation_id VARCHAR(255) NULL
);
CREATE INDEX correlation_id_idx ON events(correlation_id);
```

And then you can query from there.

See [Logging Structured Data to Database](https://tersesystems.com/blog/2019/09/18/logging-structured-data-to-database/) for more details.