
## What is Structured Logging?

Structured logging is logging data in a structure, so everything has a specific key and a value and can be parsed and processed by tools. 
 
Technically, you could be logging in another structure like XML or YAML, but almost everyone uses JSON.  

Technically, since there are several JSON objects all in one file / stream, this is called "newline delimited JSON" or [NDJSON](http://ndjson.org/) or [jsonlines](http://jsonlines.org/).  

If you only output JSON it's not a huge deal, because you can read JSON logs as text with a special log viewer such as [jl](https://github.com/koenbollen/jl/blob/master/README.md).

Semantically, a log entry typically has multiple pieces of information associated with it, described as "high cardinality" by observability geeks.  Structured logging means that the cardinality goes from "closed" -- you can only log things that you have defined fields for -- to "open", where you can add arbitrary fields and objects to your log entry as long as it's JSON.

Structured logging means that you can add more context to logs and do more with them without having to do regexes.  

## History

Structured logging has been around for a while.  Using JSON for structured logging is a little more recent.

The earliest reference I can find to JSON logging is [Logging in JSON](http://www.asynchronous.org/blog/archives/2006/01/25/logging-in-json) in 2006, followed by [Write Logs for Machines, use JSON](https://paul.querna.org/articles/2011/12/26/log-for-machines-in-json/) in 2011, both referencing Node.js.

[Structured Logging](https://www.kartar.net/2015/12/structured-logging/) in 2015 discusses JSON based logging in Ruby.

## Adding Context

There is a question of what you want to add when you log.  This is a matter of taste, but in general you should log so that you [create a consistent narrative](https://www.honeycomb.io/blog/event-foo-constructing-a-coherent-narrative/).  As previously mentioned, a log may indicate a portion of an event, so you want to log where doing so would help tell a story of what happened afterwards.

There are some things you should [always add to an event](https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/), such as who is talking to your service, what they're asking, business relevant fields, additional context around your service / environment, response time and particulars. You should add units to your field names when you measure a quantity, i.e. `response_time_ms`, and add a "human readable" version of internal information if available.

You should add [context to your logs](https://www.honeycomb.io/blog/event-foo-moar-context-better-events/) that helps differentiate it from its peers, so you never have to guess where the source of a log is coming from.

Adding a [correlation id](https://blog.rapid7.com/2016/12/23/the-value-of-correlation-ids/) helps you [design for results](https://www.honeycomb.io/blog/event-foo-designing-for-results/) and tie your logs into a coherent event.  You don't need to use a UUID: a [flake id](https://github.com/boundary/flake) will probably be better for you.  I'm using [idem](https://github.com/mguenther/idem/) here, but most things will work.

So, we know what structured logging is now.  What does it look like in SLF4J?

## Adding Structure to Logging

SLF4J doesn't have specific support for structured logging, but [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#logback-json-encoder) does.  It's complete and comprehensive, but buried in a section called [Event specific custom fields](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#event-specific-custom-fields).

Event specific custom fields are implemented in two ways: through [`net.logstash.logback.argument.StructuredArguments`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/argument/StructuredArguments.java), which adds structured information through parameters, and [`net.logstash.logback.marker.Markers`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/marker/Markers.java), which adds structured information through the `org.slf4j.Marker` API.

### StructuredArguments

`StructuredArguments` write out both to the text appenders and to the JSON appenders.  There is extra "key information" added to the JSON, and you see the value show up in the message.

```java
package example;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.*;
import static org.slf4j.LoggerFactory.*;

public class ClassWithStructuredArguments {
    private final Logger logger = getLogger(getClass());

    public void logValue(String correlationId) {
        if (logger.isInfoEnabled()) {
            logger.info("id is {}", value("correlationId", correlationId));
        }
    }

    public void logNameAndValue(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId));
    }

    public void logNameAndValueWithFormat(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId, "{0}=[{1}]"));
    }

    public void doThings(String correlationId) {
        logValue(correlationId);
        logNameAndValue(correlationId);
        logNameAndValueWithFormat(correlationId);
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.doThings(correlationId);
    }
}
```

This produces the following output in text:

```text
2019-01-20T23:24:40.004+0000 [INFO ] example.ClassWithStructuredArguments in main - id is FXtylIyzDbj9rfs7BRCAAA
2019-01-20T23:24:40.006+0000 [INFO ] example.ClassWithStructuredArguments in main - id is correlationId=FXtylIyzDbj9rfs7BRCAAA
2019-01-20T23:24:40.006+0000 [INFO ] example.ClassWithStructuredArguments in main - id is correlationId=[FXtylIyzDbj9rfs7BRCAAA]
```

and in JSON:

```json
{"@timestamp":"2019-01-20T23:24:40.004+00:00","@version":"1","message":"id is FXtylIyzDbj9rfs7BRCAAA","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
{"@timestamp":"2019-01-20T23:24:40.006+00:00","@version":"1","message":"id is correlationId=FXtylIyzDbj9rfs7BRCAAA","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
{"@timestamp":"2019-01-20T23:24:40.006+00:00","@version":"1","message":"id is correlationId=[FXtylIyzDbj9rfs7BRCAAA]","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
```

### Markers

If you want to add more context and don't want it to show up in the message, you can use [`net.logstash.logback.marker.Markers`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/marker/Markers.java) instead.

```java
package example;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    public void doThingsWithMarker(String correlationId) {
        LogstashMarker logstashMarker = Markers.append("correlationId", correlationId);
        logger.info(logstashMarker, "log with marker explicitly");
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker(correlationId);
    }
}
```

This produces the following text:

```text
2019-01-20T23:26:50.351+0000 [INFO ] example.ClassWithMarkers in main - log with marker explicitly
```

and the following JSON:

```json
{"@timestamp":"2019-01-20T23:26:50.351+00:00","@version":"1","message":"log with marker explicitly","logger_name":"example.ClassWithMarkers","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIy0T878gCNIdfWAAA"}
```

When you're using structured logging, you'll inevitably have to pass around the `LogstashMarker` or `StructuredArgument` with it so that you can add context to your logging.  In the past, the recommended way to do this was MDC.

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  MDC also breaks silently: when MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.
