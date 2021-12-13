# Ring Buffers

When something goes wrong and produces, it can be useful to keep a store of diagnostic (`DEBUG` and `TRACE`) logs on hand so that the operation leading up to to the error is available.

However, there are some constraints to logging diagnostic information -- the store must be bounded, and storage must be very fast.  One common pattern, dating all the way back to the [Apollo Mission](https://tersesystems.com/blog/2019/07/28/triggering-diagnostic-logging-on-exception/), is to store the logs in memory.  This is a pattern called ring buffer logging, described in [Using Ring Buffer Logging to Help Find Bugs](http://www.exampler.com/writing/ring-buffer.pdf) by [Brian Marick](https://twitter.com/marick).  

In ring buffer logging, all debug events related to the logger are stored, but are stored in a [circular buffer](https://en.wikipedia.org/wiki/Circular_buffer) that is overwritten by the latest logs.  When triggered, the entire buffer is flushed to appenders.  This is in contrast to tap filters, which will immediately create events and then flush them to appenders, and do not keep them in memory.

## Drawbacks

**Please consider using [Blacklite](https://github.com/tersesystems/blacklite/) over ring buffer logging**, as it's specifically designed to address very fast bounded diagnostic logging.  You can still use ring buffers, but it does have drawbacks

The biggest drawback is that logging to an in-memory ring buffer is just that: in-memory.  Logs are not accessible by external processes, and Java does not have an easy way to do external off-heap memory management.  As such, memory cannot be easily shared for IPC, and there is significant additional complexity in flushing the buffer on request, since most logging is not typically designed with explicit flushes in mind.

In addition, measuring time is a real concern with ring buffer logging.  When you dump the ring buffer contents, unless you are dumping into a JDBC database, the elements will be out of sequence to the logs as a whole.  Also, because logging to an in-memory ringbuffer is extremely fast and involves no processing, multiple events can be logged in the same millisecond.

## Using Ring Buffers 

Ring Buffers are first class objects that must be referenced at several points.  You need to set up an appender that can add logging events to a ring buffer, and another that can dump elements.  

The [showcase](https://github.com/tersesystems/terse-logback-showcase) contains an example of ringbuffer logging that we'll describe in more detail here.

To create a ring buffer, add the following actions:

```xml
<!-- loosen rule to include for encoders as well as just top level -->
<newRule pattern="*/include"
         actionClass="ch.qos.logback.core.joran.action.IncludeAction"/>

<!-- loosen the rule on appender refs so appenders can reference them -->
<newRule pattern="*/appender/appender-ref"
         actionClass="ch.qos.logback.core.joran.action.AppenderRefAction"/>

<newRule pattern="*/ringBuffer"
         actionClass="com.tersesystems.logback.ringbuffer.RingBufferAction"/>

<newRule pattern="*/ringBuffer-ref"
         actionClass="com.tersesystems.logback.ringbuffer.RingBufferRefAction"/>
```

and then define the ring buffer itself:

```xml
<ringBuffer name="JDBC_RINGBUFFER">
    <capacity>${jdbc.ringBuffer.capacity}</capacity>
</ringBuffer>
```

The ring buffer uses a multi-producer/multi-consumer array queue from [JCTools](https://github.com/JCTools/JCTools), which has [better performance than `java.util.concurrent.ArrayBlockingQueue`](https://psy-lob-saw.blogspot.com/2015/01/mpmc-multi-multi-queue-vs-clq.html).

## Adding to Ring Buffer

This works well with a JDBC appender.  

> **NOTE**: The `CorrelationIdJDBCAppender` is used here so that the `correlation_id` and `event_id` fields are available, where the `event_id` is specified using a `UniqueIdEventAppender`.  The `UniqueIdEventAppender` decorates logging events to have a [flake id](http://yellerapp.com/posts/2015-02-09-flake-ids.html) using [idem](https://github.com/mguenther/idem).  Because inserts into a database can happen out of order and the timestamp does not have enough resolution, a flake id is the only sure way to keep ordering consistent.

```scala
<appender name="ASYNC_JDBC" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
    <!--
      A JDBC appender that adds the correlation id as a field.
    -->
    <appender class="com.tersesystems.logback.correlationid.CorrelationIdJDBCAppender">
       <!-- ... -->
    </appender>
</appender>

<!-- assume something like this for unique event id appender -->
<root>
    <appender class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
        <appender-ref ref="ASYNC_JDBC"/>
    </appender>
</root>
```

Appending to a ring buffer is done with `com.tersesystems.logback.ringbuffer.RingBufferAwareAppender`.  The logic on `RingBufferAwareAppender` is different from most other appenders in that any logging events that are rejected by the filter will be logged to the ringbuffer without further processing.

```xml
<appender name="ASYNC_JDBC_WITH_RINGBUFFER" class="com.tersesystems.logback.ringbuffer.RingBufferAwareAppender">
    <!-- log to jdbc if INFO or above, otherwise log to ring buffer -->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>${jdbc.threshold}</level>
    </filter>

    <!-- anything denied goes to the ring buffer -->
    <ringBuffer-ref ref="JDBC_RINGBUFFER"/>

    <!-- anything accepted goes to the JDBC appender -->
    <appender-ref ref="ASYNC_JDBC"/>
</appender>
```

## Dumping Ring Buffers

Finally, there needs to be a way to dump the contents of a ring buffer.  This is done from a targetted logger that contains an instance of `com.tersesystems.logback.ringbuffer.DumpRingBufferAppender`.  The entire contents of the ring buffer is drained to the appender.  

> **NOTE**: If you are draining to an asynchronous logger, you should ensure that the queue is large enough to accommodate the contents of the entire ring buffer **in addition to normal traffic.**  This is because many asynchronous loggers such as [AsyncAppender](http://logback.qos.ch/manual/appenders.html#AsyncAppender) are lossy and will drop events if the queue fills up passes a comfortable threshold.

```xml
<logger name="JDBC_RINGBUFFER_LOGGER" level="TRACE" additivity="false">
    <!-- This appender dumps contents of the ring buffer when an event is received. -->
    <appender class="com.tersesystems.logback.ringbuffer.DumpRingBufferAppender">
        <!-- Event source -->
        <ringBuffer-ref ref="JDBC_RINGBUFFER"/>

        <!-- Event source -->
        <appender-ref ref="ASYNC_JDBC"/>
    </appender>
</logger>
```

Once you've defined a logger, you can trigger a dump just by logging to that specific appender:

```java
Logger bufferControl = LoggerFactory.getLogger("JDBC_RINGBUFFER_LOGGER");
bufferControl.error("Dump the ringbuffer to JDBC here!");
```

## RingBuffer Markers

A marker factory that contains a ringbuffer and two inner classes, RecordMarker and DumpMarker.

Using a ring buffer marker factory means that you can build up a thread of messages and dump the ring buffer at a later point, for example:

```java
public class Foo {
  public void logAndDump() {   
    RingBuffer ringBuffer = getRingBuffer();
    RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(ringBuffer);
    Marker recordMarker = markerFactory.createRecordMarker();
    Marker dumpMarker = markerFactory.createTriggerMarker();
     
    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.debug(recordMarker, "debug one");
    logger.debug(recordMarker, "debug two");
    logger.debug(recordMarker, "debug three");
    logger.debug(recordMarker, "debug four");
    logger.error(dumpMarker, "Dump all the messages"); 
  }
}
```

## Further Reading

See [Triggering Diagnostic Logging on Exception](https://tersesystems.com/blog/2019/07/28/triggering-diagnostic-logging-on-exception/) and [Diagnostic Logging: Citations and Sources](https://tersesystems.com/blog/2019/10/05/diagnostic-logging-citations-and-sources/) for more details.
