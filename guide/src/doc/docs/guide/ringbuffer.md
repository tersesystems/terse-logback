

## Triggering Diagnostic Logging on Exception

There may be situations where there is no visible target for diagnostic logging, for example in the case where there is a race condition or a subtle data corruption that only shows up every so often.  In this case, the ideal workflow would be to keep the most recent diagnostic information available, but only see it when the appropriate condition is triggered.

This is a pattern called ring buffer logging, described in [Using Ring Buffer Logging to Help Find Bugs](http://www.exampler.com/writing/ring-buffer.pdf) by [Brian Marick](https://twitter.com/marick).  In ring buffer logging, all debug events related to the logger are stored, but are stored in a [circular buffer](https://en.wikipedia.org/wiki/Circular_buffer) that is overwritten by the latest logs.  When triggered, the entire buffer is flushed to appenders.  This is in contrast to tap filters, which will immediately create events and then flush them to appenders, and do not keep them in memory.

There are two implementations of ring buffer logging, in the `logback-ringbuffer` module: one that is threshold based, and another which is marker based.

**NOTE**: When using ring buffers, you should be aware that **anything stored in the ring buffer will not be garbage collected** until the ring buffer is flushed or the reference is overwritten -- if you are passing in any large objects through argument parameters or through marker references, they may hang around much longer than you expect.  Please be judicious and use [weak references](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html) if necessary.

### Threshold Based Ring Buffer

The threshold ring buffer is implemented as `com.tersesystems.logback.ringbuffer.ThresholdRingBufferTurboFilter`.  You can specify `logger` by name or by package, to indicate what loggers you want to record diagnostic events on.  You can specify the record level, which is usually `DEBUG` or `TRACE`, and logging statements that are equal to or below that record level will be added.  The trigger level, which is usually `WARN` or `ERROR`, indicates the threshold at which the ring buffered statements will be flushed to the appenders and the ring buffer cleared.

```xml
<configuration>

    <turboFilter class="com.tersesystems.logback.ringbuffer.ThresholdRingBufferTurboFilter">
        <logger>com.example.Test</logger>
        <recordLevel>DEBUG</recordLevel>
        <triggerLevel>ERROR</triggerLevel>
    </turboFilter>

</configuration>
```

Threshold based ring buffer is singular: there is only one for the entire logging context.  If you want to have different ring buffers or more complex logic, you may want to use a marker based ring buffer instead.

### Marker Based Ring Buffer

If you want to have diagnostic events displayed as part of the log message, then you can use `MarkerEventRingBufferTurboFilter` from the `ringbuffer-event` module.  This code depends on `logstash-logback-encoder` to encode the logging events inside the error logging statement.

To use, add the following turbomarker:

```xml
<configuration>
    <turboFilter class="com.tersesystems.logback.ringbuffer.event.MarkerEventRingBufferTurboFilter">
    </turboFilter>
</configuration>
```

and then the given code:

```java

public class MarkerEventRingBufferTurboFilterTest {
    @Test
    public void testWithDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(10);
        Marker recordMarker = markerFactory.createRecordMarker();
        Marker dumpMarker = markerFactory.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.debug(recordMarker, "debug one");
        logger.debug(recordMarker, "debug two");
        logger.debug(recordMarker, "debug three");
        logger.debug(recordMarker, "debug four");
        logger.error(dumpMarker, "Dump all the messages");

        System.out.println(dumpAsJson(listAppender.list.get(0), loggerFactory));
    }
}
```

yields the following JSON:

```json
{
  "@timestamp": "2019-08-02T07:33:47.097-07:00",
  "@version": "1",
  "message": "Dump all the messages",
  "logger_name": "com.example.Test",
  "thread_name": "main",
  "level": "ERROR",
  "level_value": 40000,
  "tags": [
    "TS_DUMP_MARKER"
  ],
  "diagnosticEvents": [
    {
      "@timestamp": "2019-08-02T07:33:47.094-07:00",
      "@version": "1",
      "message": "debug one",
      "logger_name": "com.example.Test",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000,
      "tags": [
        "TS_RECORD_MARKER"
      ]
    },
    {
      "@timestamp": "2019-08-02T07:33:47.094-07:00",
      "@version": "1",
      "message": "debug two",
      "logger_name": "com.example.Test",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000,
      "tags": [
        "TS_RECORD_MARKER"
      ]
    },
    {
      "@timestamp": "2019-08-02T07:33:47.094-07:00",
      "@version": "1",
      "message": "debug three",
      "logger_name": "com.example.Test",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000,
      "tags": [
        "TS_RECORD_MARKER"
      ]
    },
    {
      "@timestamp": "2019-08-02T07:33:47.094-07:00",
      "@version": "1",
      "message": "debug four",
      "logger_name": "com.example.Test",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000,
      "tags": [
        "TS_RECORD_MARKER"
      ]
    }
  ]
}
```

### Appender based Ring Buffer

Finally, there's `AppenderRingBufferTurboFilter`, a turbo filter which uses the cyclic buffer defined as an appender: on an event that matches the trigger level, the events are pulled from the cyclic appender and inserted into the message, and the cyclic buffer is cleared.  This is in the `logback-ringbuffer-appender` module.

This may be an easier fit for some applications vs `ThresholdRingBufferTurboFilter`, as you can leave the processing logic mostly the same, and specify filters on the appenders as normal.

Note that we use an `EncodingRingBufferAppender` here, which processes the event and stores JSON internally in the ring buffer.  This means more CPU processing since serialization to JSON happens on all debug events, but it does mean that all references in logging events are freed up when processed.

This does require a bit more configuration:

```xml
<configuration>
    <!-- appender-ref is only on "root" so tweak it for turbofilter -->
    <newRule pattern="configuration/turboFilter/appender-ref"
             actionClass="ch.qos.logback.core.joran.action.AppenderRefAction"/>
  
    <!-- appender has to be defined before turbo filter since we reference it -->
    <appender name="DEBUG-CYCLIC" class="com.tersesystems.logback.classic.EncodingRingBufferAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        </encoder>
    </appender>

    <turboFilter class="com.tersesystems.logback.ringbuffer.appender.AppenderRingBufferTurboFilter">
        <appender-ref ref="DEBUG-CYCLIC"/>
        <recordLevel>DEBUG</recordLevel>
        <triggerLevel>ERROR</triggerLevel>
    </turboFilter>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        </encoder>
    </appender>

    <!-- Turn on debugging and it will go into the cyclic buffer  -->
    <logger name="com.example.Debug" level="DEBUG"/>

    <root level="INFO">
        <appender-ref ref="DEBUG-CYCLIC" />
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

then given the following code:

```java
public class AppenderRingBufferTurboFilterTest {
    @Test
    public void testWithDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        // These go into the cyclic buffer because com.example.Debug is on
        Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
        debugLogger.debug("debug one");
        debugLogger.debug("debug two");
        debugLogger.debug("debug three");
        debugLogger.debug("debug four");

        // These don't go into the debug appender, because the logger is not set to DEBUG level
        Logger debugOffLogger = loggerFactory.getLogger("com.example.NotDebug");
        debugOffLogger.debug("this does not get added");

        // An error statement dumps and flushes the cyclic barrier.
        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.error( "Dump all the messages");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(1);        
    }
}
```

yields the following:

```json
{
  "@timestamp": "2019-08-04T19:34:18.653-07:00",
  "@version": "1",
  "message": "Dump all the messages",
  "logger_name": "com.example.Test",
  "thread_name": "main",
  "level": "ERROR",
  "level_value": 40000,
  "diagnosticEvents": [
    {
      "@timestamp": "2019-08-04T19:34:18.650-07:00",
      "@version": "1",
      "message": "debug one",
      "logger_name": "com.example.Debug",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000
    },
    {
      "@timestamp": "2019-08-04T19:34:18.650-07:00",
      "@version": "1",
      "message": "debug two",
      "logger_name": "com.example.Debug",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000
    },
    {
      "@timestamp": "2019-08-04T19:34:18.650-07:00",
      "@version": "1",
      "message": "debug three",
      "logger_name": "com.example.Debug",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000
    },
    {
      "@timestamp": "2019-08-04T19:34:18.650-07:00",
      "@version": "1",
      "message": "debug four",
      "logger_name": "com.example.Debug",
      "thread_name": "main",
      "level": "DEBUG",
      "level_value": 10000
    }
  ]
}
```
