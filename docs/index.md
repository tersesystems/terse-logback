# Terse Logback

Terse Logback is a collection of [Logback](https://logback.qos.ch/) modules that extend [Logback](https://logback.qos.ch/manual/index.html) functionality.  

I've written about the reasoning and internal architecture in a series of blog posts.  The [full list](https://tersesystems.com/category/logging/) is available on [https://tersesystems.com](https://tersesystems.com).

## Showcase

If you want to see a running application, there is a [showcase web application](https://github.com/tersesystems/terse-logback-showcase) that run out of the box that demonstrates some of the more advanced features, and shows you can integrate terse-logback with [Sentry](https://sentry.io) and [Honeycomb](https://www.honeycomb.io).

## Modules

- [Audio](guide/audio.md): Play audio when you log by attaching markers to your logging statements.
- [Budgeting / Rate Limiting](guide/budget.md): Limit the amount of debugging or tracing statements in a time period.
- [Censors](guide/censor.md): Censor sensitive information in logging statements.     
- [Composite](guide/composite.md): Presents a single appender that composes several appenders. 
- [Compression](guide/compression.md): Write to a compressed zstandard file. 
- [Correlation Id](guide/correlationid.md): Adds markers and filters for correlation id.
- [Exception Mapping](guide/exception-mapping.md): Show the important details of an exception, including the root cause in a summary format.
- [Instrumentation](guide/instrumentation.md): Decorates any (including JVM) class with enter and exit logging statements at runtime.
- [JDBC](guide/jdbc.md): Use Postgres JSON to write structured logging to a single table.
- [JUL to SLF4J Bridge](guide/slf4jbridge.md): Configure java.util.logging to write to SLF4J with no [manual coding](https://mkyong.com/logging/how-to-load-logging-properties-for-java-util-logging/).
- [Relative Nanos](guide/relativens.md): Composes a logging event to contain relative nanoseconds based off `System.nanoTime`.
- [Select Appender](guide/select.md): Appender that selects an appender from a list based on key.
- [Tracing](guide/tracing.md): Sends logging events and traces to [Honeycomb Event API](https://docs.honeycomb.io/api/events/).
- [Typesafe Config](guide/typesafeconfig.md): Configure Logback properties using [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md).
- [Turbo Markers](guide/turbomarker.md): [Turbo Filters](https://logback.qos.ch/manual/filters.html#TurboFilter) that depend on arbitrary deciders that can log at debug level for sessions. 
- [Unique ID Appender](guide/uniqueid.md): Composes logging event to contain a unique id across multiple appenders. 
