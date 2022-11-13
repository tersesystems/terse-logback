# Correlation ID

The `logback-correlationid` module is a set of classes designed to encompass the idea of a correlation id in events.

## Installation

Add the library dependency using [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-correlationid](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-correlationid).

## Usage

It consists of a correlation id filter, a tap filter that always logs events with a correlation id to an appender, and a correlation id marker.

### Correlation ID Filter

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

### CorrelationIdTapFilter

The `CorrelationIdTapFilter` is a turbofilter that always logs to a given appender if the correlation id appears, even if the appender is not configured for logging.  

This functions as a <a href="https://www.enterpriseintegrationpatterns.com/patterns/messaging/WireTap.html">wiretap</a>.

Tap Filters are very useful as a way to send data to an appender.  They completely bypass any kind of logging level configured on the front end, so you can set a logger to INFO level but still have access to all TRACE events when an error occurs, through the tap filter's appenders.

For example, a tap filter can automatically log everything with a correlation id at a TRACE level, without requiring filters or altering the log level as a whole.  Let's run a simple HTTP client program that calls out to Google and prints a result.


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

## CorrelationIdUtils

`CorrelationIdUtils` contains utility methods like `get` which retrieve a correlation id from either a marker or MDC.
