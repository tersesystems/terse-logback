[![Maven Central](https://img.shields.io/maven-central/v/com.tersesystems.logback/logback-classic)](https://search.maven.org/search?q=g:com.tersesystems.logback) [![License CC0](https://img.shields.io/badge/license-CC0-blue.svg)](https://tldrlegal.com/license/creative-commons-cc0-1.0-universal)

# Terse Logback

Terse Logback is a collection of [Logback](https://logback.qos.ch/) extensions that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively. 

Other logging projects you may be interested in: 

* [Blacklite](https://github.com/tersesystems/blacklite/), an SQLite appender with memory-mapping and zstandard dictionary compression that clocks around 800K statements per second.
* [Blindsight](https://github.com/tersesystems/blindsight), a Scala logging API that extends SLF4J.
* [Echopraxia](https://github.com/tersesystems/echopraxia), a Java and Scala logging API built around structured logging.

## Documentation

Documentation is available at [https://tersesystems.github.io/terse-logback](https://tersesystems.github.io/terse-logback/1.0.3).

## Showcase

There is a showcase project at [https://github.com/tersesystems/terse-logback-showcase](https://github.com/tersesystems/terse-logback-showcase).

## Modules

- [Audio](https://tersesystems.github.io/terse-logback/guide/audio): Play audio when you log by attaching markers to your logging statements.
- [Budgeting / Rate Limiting](https://tersesystems.github.io/terse-logback/guide/budget): Limit the amount of debugging or tracing statements in a time period.
- [Censors](https://tersesystems.github.io/terse-logback/guide/censor): Censor sensitive information in logging statements.
- [Composite](https://tersesystems.github.io/terse-logback/guide/composite): Presents a single appender that composes several appenders.
- [Compression](https://tersesystems.github.io/terse-logback/guide/compression): Write to a compressed zstandard file.
- [Correlation Id](https://tersesystems.github.io/terse-logback/guide/correlationid): Adds markers and filters for correlation id.
- [Exception Mapping](https://tersesystems.github.io/terse-logback/guide/exception-mapping): Show the important details of an exception, including the root cause in a summary format.
- [Instrumentation](https://tersesystems.github.io/terse-logback/guide/instrumentation): Decorates any (including JVM) class with enter and exit logging statements at runtime.
- [JDBC](https://tersesystems.github.io/terse-logback/guide/jdbc): Use Postgres JSON to write structured logging to a single table.
- [JUL to SLF4J Bridge](https://tersesystems.github.io/terse-logback/guide/slf4jbridge): Configure java.util.logging to write to SLF4J with no [manual coding](https://mkyong.com/logging/how-to-load-logging-properties-for-java-util-logging/).
- [Relative Nanos](https://tersesystems.github.io/terse-logback/guide/relativens): Composes a logging event to contain relative nanoseconds based off `System.nanoTime`.
- [Select Appender](https://tersesystems.github.io/terse-logback/guide/select): Appender that selects an appender from a list based on key.
- [Tracing](https://tersesystems.github.io/terse-logback/guide/tracing): Sends logging events and traces to [Honeycomb Event API](https://docs.honeycomb.io/api/events/).
- [Typesafe Config](https://tersesystems.github.io/terse-logback/guide/typesafeconfig): Configure Logback properties using [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md).
- [Turbo Markers](https://tersesystems.github.io/terse-logback/guide/turbomarker): [Turbo Filters](https://logback.qos.ch/manual/filters.html#TurboFilter) that depend on arbitrary deciders that can log at debug level for sessions.
- [Unique ID Appender](https://tersesystems.github.io/terse-logback/guide/uniqueid): Composes logging event to contain a unique id across multiple appenders. 