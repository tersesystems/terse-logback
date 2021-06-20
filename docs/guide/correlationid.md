# Correlation ID

The `logback-correlationid` module is a set of classes designed to encompass the idea of a correlation id in events.

It consists of a correlation id filter, a tap filter that always logs events with a correlation id to an appender, a JDBC appender that writes correlation id to a column in a database schema, and a correlation id marker. 

## Correlation ID Filter

A correlation id filter will filter for a correlation id set either as an MDC value, or as a marker created from `CorrelationIdMarker`.  

```xml
  <appender name="LIST" class="ch.qos.logback.core.read.ListAppender">
    <filter class="com.tersesystems.logback.correlationid.CorrelationIdFilter">
        <mdcKey>correlationId</mdcKey>
    </filter>
</appender>
```

If an appender passes the filter, it will log the event.

```java
public class CorrelationIdFilterTest {
    public void testFilter() {
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
    }
}
```

## CorrelationIdTapFilter

The `CorrelationIdTapFilter` is a turbofilter that always logs to a given appender if the correlation id appears, even if the appender is not configured for logging.

```xml
<configuration>

    <newRule pattern="configuration/turboFilter/appender-ref"
             actionClass="ch.qos.logback.core.joran.action.AppenderRefAction"/>

    <appender name="TAP_LIST" class="ch.qos.logback.core.read.ListAppender">
    </appender>

    <turboFilter class="com.tersesystems.logback.correlationid.CorrelationIdTapFilter">
        <mdcKey>correlationId</mdcKey>
        <appender-ref ref="TAP_LIST"/>
    </turboFilter>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

## CorrelationIdMarker

A `CorrelationIdMarker` implements the `CorrelationIdProvider` interface to expose a marker which is known to contain a correlation id.

```java
CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create(correlationId);
String sameId = correlationIdMarker.getCorrelationId();
```

## CorrelationIdJDBCAppender

A `CorrelationIdJDBCAppender` is a JDBC appender that can write out a correlation id to a row, extending the normal JDBC correlator.

```xml
<configuration>

    <appender name="ASYNC_JDBC" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender class="com.tersesystems.logback.correlationid.CorrelationIdJDBCAppender">
            <mdcKey>correlationId</mdcKey>

            <!--          <driver>com.p6spy.engine.spy.P6SpyDriver</driver>-->
            <!--          <url>jdbc:p6spy:h2:mem:terse-logback;DB_CLOSE_DELAY=-1</url>-->
            <driver>org.h2.Driver</driver>
            <url>jdbc:h2:mem:terse-logback;DB_CLOSE_DELAY=-1</url>
            <username>sa</username>
            <password></password>

            <createStatements>
                CREATE TABLE IF NOT EXISTS events (
                ID NUMERIC NOT NULL PRIMARY KEY AUTO_INCREMENT,
                ts TIMESTAMP(9) WITH TIME ZONE NOT NULL,
                relative_ns BIGINT NULL,
                start_ms BIGINT NULL,
                level_value int NOT NULL,
                level VARCHAR(7) NOT NULL,
                evt JSON NOT NULL,
                correlation_id VARCHAR(255) NOT NULL,
                event_id VARCHAR(255) NULL
                );
                CREATE INDEX IF NOT EXISTS event_id_idx ON events(event_id);
                CREATE INDEX IF NOT EXISTS correlation_id_idx ON events(correlation_id);
            </createStatements>
            <insertStatement>
                insert into events(ts, relative_ns, start_ms, level_value, level, evt, correlation_id, event_id) values(?, ?, ?, ?, ?, ?, ?, ?)
            </insertStatement>

            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            </encoder>
        </appender>
    </appender>

</configuration>
```

## CorrelationIdUtils

`CorrelationIdUtils` contains utility methods like `get` which retrieve a correlation id from either a marker or MDC.