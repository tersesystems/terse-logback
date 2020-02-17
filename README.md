<!---freshmark shields
output = [
	link(shield('Bintray', 'bintray', 'tersesystems:terse-logback', 'blue'), 'https://bintray.com/tersesystems/maven/terse-logback/view'),
	link(shield('Latest version', 'latest', '0.13.3', 'blue'), 'https://github.com/tersesystems/terse-logback/releases/latest'),
	link(shield('License CC0', 'license', 'CC0', 'blue'), 'https://tldrlegal.com/license/creative-commons-cc0-1.0-universal'),
	'',
	link(shield('Release Notes', 'release-notes', '{{previousVersion}}', 'brightgreen'), 'docs/release-notes.md'),
	link(image('Travis CI', 'https://travis-ci.org/tersesystems/terse-logback.svg?branch=master'), 'https://travis-ci.org/tersesystems/terse-logback')
	].join('\n')
-->
[![Bintray](https://img.shields.io/badge/bintray-tersesystems%3Aterse--logback-blue.svg)](https://bintray.com/tersesystems/maven/terse-logback/view)
[![Latest version](https://img.shields.io/badge/latest-0.13.3-blue.svg)](https://github.com/tersesystems/terse-logback/releases/latest)
[![License CC0](https://img.shields.io/badge/license-CC0-blue.svg)](https://tldrlegal.com/license/creative-commons-cc0-1.0-universal)

[![Release Notes](https://img.shields.io/badge/release--notes-0.13.2-brightgreen.svg)](docs/release-notes.md)
[![Travis CI](https://travis-ci.org/tersesystems/terse-logback.svg?branch=master)](https://travis-ci.org/tersesystems/terse-logback)
<!---freshmark /shields -->

# Terse Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  It also serves as a demonstration of what you can do with Logback and logging in general, including some features that may not be obvious at first glance.

It shows how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

I've written about the reasoning and internal architecture in a series of blog posts.  The [full list](https://tersesystems.com/category/logging/) is available on [https://tersesystems.com](https://tersesystems.com).

## Quickstart

You want to start up a project immediately and figure things out?  Okay then.

The project is configured into several modules.  The most relevant one to start with is [`logback-structured-config`](https://github.com/tersesystems/terse-logback/tree/master/logback-structured-config/src/main/resources) which shows a finished project put together.  

The `logback-structured-config` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.

You can see it on [mvnrepository](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-structured-config) but you will need a custom resolver, so better to read through the whole thing.

This is [not intended](https://tersesystems.com/blog/2019/04/23/application-logging-in-java-part-1/) to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.

### Maven

Add the following repository:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xsi:schemaLocation='http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd'
          xmlns='http://maven.apache.org/SETTINGS/1.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>
    
    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                    <id>bintray-tersesystems-maven</id>
                    <name>bintray</name>
                    <url>https://dl.bintray.com/tersesystems/maven</url>
                </repository>
            </repositories>
            <id>bintray</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>bintray</activeProfile>
    </activeProfiles>
</settings>
```

Create a subproject `logging` and make your main codebase depend on it, but only provide `slf4j-api` to the main codebase.

```xml
<dependency>
  <groupId>com.tersesystems.logback</groupId>
  <artifactId>logback-structured-config</artifactId>
  <version>$latestVersion</version>
  <type>pom</type>
</dependency>
```

### Gradle

Add the following resolver:

```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/tersesystems/maven" 
    }
}
```

Create a subproject `logging` and make your main codebase depend on it, but only provide `slf4j-api` to the main codebase.  In the logging project, add the following:

```
implementation 'com.tersesystems.logback:logback-structured-config:<latestVersion>'
```

### SBT

Create an SBT subproject and include it with your main build.

```
lazy val logging = (project in file("logging")).settings(
    resolvers += Resolver.bintrayRepo("tersesystems", "maven"),
    libraryDependencies += "com.tersesystems.logback" % "logback-structured-config" % "<latestVersion>"
)

lazy val impl = (project in file("impl")).settings(
  // all your code dependencies + slf4j-api
  libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
).dependsOn(logging)

lazy val root = project in file(".").aggregate(logging, impl)
```

### Configuration

After you've set up the resolvers and library dependencies for your build, you'll add the following to `src/main/resources/logback.xml`:

```xml
<configuration debug="true">
  <include resource="terse-logback/default.xml"/>
</configuration>
```

Then add a `logback.conf` file that contains the following:

```hocon
levels {
  ROOT = DEBUG
}
```

That should give you a fairly verbose logging setup and allow you to change the configuration.  See the [reference section](https://github.com/tersesystems/terse-logback#logback-xml-with-custom-actions) for more details.


You may also want to look at https://github.com/wsargent/sbt-with-jdk-13-docker-logging-example which leverages [sbt-native-packager](https://www.scala-sbt.org/sbt-native-packager/index.html) to provide different logging behavior.

## What is Structured Logging?

It's logging data in a structure, so everything has a specific key and a value and can be parsed and processed by tools.  Technically, you could be logging in another structure like XML or YAML, but almost everyone uses JSON.  It's been around for [a while](https://www.kartar.net/2015/12/structured-logging/).  Technically, since there are several JSON objects all in one file / stream, this is called "newline delimited JSON" or [NDJSON](http://ndjson.org/) or [jsonlines](http://jsonlines.org/).  In this project, both text and JSON formats are rendered independently, but if you only output JSON it's not a huge deal, because you can read JSON logs as text with a special log viewer such as [jl](https://github.com/koenbollen/jl/blob/master/README.md).

Semantically, a log entry typically has multiple pieces of information associated with it, described as "high cardinality" by observability geeks.  Structured logging means that the cardinality goes from "closed" -- you can only log things that you have defined fields for -- to "open", where you can add arbitrary fields and objects to your log entry as long as it's JSON.

Structured logging means that you can add more context to logs and do more with them without having to do regexes.  

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

## Logging to Honeycomb

You can connect Logback to Honeycomb directly through the Honeycomb Logback appender.  The appender is split into the appender and an HTTP client implementation, which can be OKHTTP or Play WS.

Add the appender module 'logback-honeycomb-appender' and the implementation 'logback-honeycomb-okhttp':

```gradle
compile group: 'com.tersesystems.logback', name: 'logback-tracing'
compile group: 'com.tersesystems.logback', name: 'logback-honeycomb-appender'
compile group: 'com.tersesystems.logback', name: 'logback-honeycomb-okhttp'
```

The appender is as follows:

```xml
<configuration>
  <conversionRule conversionWord="startTime" converterClass="com.tersesystems.logback.classic.StartTimeConverter" />

  <appender name="HONEYCOMB" class="com.tersesystems.logback.honeycomb.HoneycombAppender">
      <apiKey>${HONEYCOMB_API_KEY}</apiKey>
      <dataSet>terse-logback</dataSet>
      <sampleRate>1</sampleRate>
      <queueSize>10</queueSize>
      <batch>true</batch>
      <includeCallerData>false</includeCallerData>

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

  <!-- 
    don't send the logs from the http engine to the appender or you
    may end up in a loop
  -->
  <logger name="okhttp" level="ERROR"/>

  <root level="INFO">
      <appender-ref ref="HONEYCOMB" />
  </root>
</configuration>
```

You can also send tracing information to Honeycomb through SLF4J markers, using the `SpanMarkerFactory`.  Underneath the hood, the SpanInfo puts together logstash markers according to [manual tracing](https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#manual-tracing).

The way this works in practice is that you start up a `SpanInfo` at the beginning of a request, and call `buildNow` to mark the start of the span.  At the end of the operation, you log with a marker, by passing through the marker factory:

```java
SpanInfo spanInfo = builder.setRootSpan("index").buildNow();
// ...
logger.info(markerFactory.apply(spanInfo), "completed successfully!");
```

If you want to create a child span, you can do it from the parent using `withChild`:

```java
return spanInfo.withChild("doSomething", childInfo -> {
   return doSomething(childInfo);
});
```

or asking for a child builder that you can build yourself:


```java
SpanInfo childInfo = spanInfo.childBuilder().setSpanName("doSomething").buildNow();
```

The start time information is captured in a `StartTimeMarker` which can be extracted by `StartTime.from`.  The event timestamp serves as the span's end time.

For example, in Play you might run a controller as follows:

```scala
import com.tersesystems.logback.tracing.SpanMarkerFactory
import com.tersesystems.logback.tracing.SpanInfo
import javax.inject._
import org.slf4j.LoggerFactory
import play.api.libs.concurrent.Futures
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, futures: Futures)
  (implicit ec: ExecutionContext) extends AbstractController(cc) {
  private val markerFactory = new SpanMarkerFactory()

  private val logger = LoggerFactory.getLogger(getClass)

  private def builder: SpanInfo.Builder = SpanInfo.builder().setServiceName("play_hello_world")

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val spanInfo = builder.setRootSpan("index").buildNow()

    val f: Future[Result] = spanInfo.withChild("renderPage", renderPage(_))
    f.andThen {
      case Success(_) =>
        logger.info(markerFactory(spanInfo), "index completed successfully!")
      case Failure(e) =>
        logger.error(markerFactory(spanInfo), "index completed with error", e)
    }
  }

  def renderPage(spanInfo: SpanInfo): Future[Result] = {
    futures.delay(5.seconds).map { _ =>
      Ok(views.html.index())
    }.andThen {
       case Success(_) =>
         logger.info(markerFactory(spanInfo), "renderPage completed successfully!")
       case Failure(e) =>
         logger.error(markerFactory(spanInfo), "renderPage completed with error", e)
    }
  }
}
```

This generates a trace with a root span of "index", a child span of "renderPage" each with their own durations.

You can also send [span events](https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#span-events) and [span links](https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#links) using the `LinkMarkerFactory` and `EventMarkerFactory`, similar to the `SpanMarkerFactory`.

## Logging to Database

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

The JDBC appender can be extended so you can add extra information to the table.  In the `logback-correlationid` module, there's a `CorrelationIdJdbcAppender` that adds extra information into the event so you can query by the correlation id specifically, by using the `insertAdditionalData` hook:

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

## Selectively Logging with TurboMarkers

[Turbo filters](https://logback.qos.ch/manual/filters.html#TurboFilter) are filters that decide whether a logging event should be created or not.  They are are not appender specific in the way that normal filters are, and so are used to override logger levels.  However, there's a problem with the way that the turbo filter is set up: the two implementing classes are `ch.qos.logback.classic.turbo.MarkerFilter` and `ch.qos.logback.classic.turbo.MDCFilter`.  The marker filter will always log if the given marker is applied, and the MDC filter relies on an attribute being populated in the MDC map.

What we'd really like to do is say "for this particular user, log everything he does at DEBUG level" and not have it rely on thread-local state at all, and carry out an arbitrary computation at call time.

We start by pulling the `decide` method to an interface, [`TurboFilterDecider`](https://github.com/tersesystems/terse-logback/blob/master/logback-classic/src/main/java/com/tersesystems/logback/classic/TurboFilterDecider.java):

```java
public interface TurboFilterDecider {
    FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t);
}
```

And have the turbo filter [delegate to markers that implement the TurboFilterDecider interface](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/TurboMarkerTurboFilter.java):

```java
public class TurboMarkerTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        // ...
    }

    private FilterReply evaluateMarker(Marker marker, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (marker instanceof TurboFilterDecider) {
            TurboFilterDecider decider = (TurboFilterDecider) marker;
            return decider.decide(rootMarker, logger, level, format, params, t);
        }
        return FilterReply.NEUTRAL;
    }
}
```

This gets us part of the way there.  We can then set up a [`ContextAwareTurboFilterDecider`](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/ContextAwareTurboFilterDecider.java), which does the same thing but assumes that you have a type `C` that is your external context.

```java
public interface ContextAwareTurboFilterDecider<C> {
    FilterReply decide(ContextAwareTurboMarker<C> marker, C context, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t);
}
```

Then we add a marker class that [incorporates that context in decision making](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/ContextAwareTurboMarker.java):

```java
public class ContextAwareTurboMarker<C> extends TurboMarker implements TurboFilterDecider {
    private final C context;
    private final ContextAwareTurboFilterDecider<C> contextAwareDecider;
    // ... initializers and such
    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return contextAwareDecider.decide(this, context, rootMarker, logger, level, format, params, t);
    }
}
```

This may look good in the abstract, but it may make more sense to see it in action.  To do this, we'll set up an example application context:

```java
public class ApplicationContext {

    private final String userId;

    public ApplicationContext(String userId) {
        this.userId = userId;
    }

    public String currentUserId() {
        return userId;
    }
}
```

and a factory that contains the decider:

```java
import com.tersesystems.logback.turbomarker.*;

public class UserMarkerFactory {

    private final Set<String> userIdSet = new ConcurrentSkipListSet<>();

    private final ContextDecider<ApplicationContext> decider = context ->
        userIdSet.contains(context.currentUserId()) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;

    public void addUserId(String userId) {
        userIdSet.add(userId);
    }

    public void clear() {
        userIdSet.clear();
    }

    public UserMarker create(ApplicationContext applicationContext) {
        return new UserMarker("userMarker", applicationContext, decider);
    }
}
```

and a `UserMarker`, which is only around for the logging evaluation:

```java
public class UserMarker extends ContextAwareTurboMarker<ApplicationContext> {
    public UserMarker(String name,
                      ApplicationContext applicationContext,
                      ContextAwareTurboFilterDecider<ApplicationContext> decider) {
        super(name, applicationContext, decider);
    }
}
```

and then we can set up logging that will only work for user "28":

```java
String userId = "28";
ApplicationContext applicationContext = new ApplicationContext(userId);
UserMarkerFactory userMarkerFactory = new UserMarkerFactory();
userMarkerFactory.addUserId(userId); // say we want logging events created for this user id

UserMarker userMarker = userMarkerFactory.create(applicationContext);

logger.info(userMarker, "Hello world, I am info and log for everyone");
logger.debug(userMarker, "Hello world, I am debug and only log for user 28");
```

This works especially well with a configuration management service like [Launch Darkly](https://docs.launchdarkly.com/docs/java-sdk-reference#section-variation), where you can [target particular users](https://docs.launchdarkly.com/docs/targeting-users#section-assigning-users-to-a-variation) and set up logging based on the user variation.  

The LaunchDarkly blog has [best practices for operational flags](https://launchdarkly.com/blog/operational-flags-best-practices/):

> Verbose logs are great for debugging and troubleshooting but always running an application in debug mode is not viable. The amount of log data generated would be overwhelming. Changing logging levels on the fly typically requires changing a configuration file and restarting the application. A multivariate operational flag enables you to change the logging level from WARNING to DEBUG in real-time.

But we can give an example using the Java SDK.  You can set up a factory like so:

```java
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;

public class LDMarkerFactory {
    private final LaunchDarklyDecider decider;

    public LDMarkerFactory(LDClientInterface client) {
        this.decider = new LaunchDarklyDecider(requireNonNull(client));
    }

    public LDMarker create(String featureFlag, LDUser user) {
        return new LDMarker(featureFlag, user, decider);
    }

    static class LaunchDarklyDecider implements MarkerContextDecider<LDUser> {
        private final LDClientInterface ldClient;

        LaunchDarklyDecider(LDClientInterface ldClient) {
            this.ldClient = ldClient;
        }

        @Override
        public FilterReply apply(ContextAwareTurboMarker<LDUser> marker, LDUser ldUser) {
            return ldClient.boolVariation(marker.getName(), ldUser, false) ?
                    FilterReply.ACCEPT :
                    FilterReply.NEUTRAL;
        }
    }

    public static class LDMarker extends ContextAwareTurboMarker<LDUser> {
        LDMarker(String name, LDUser context, ContextAwareTurboFilterDecider<LDUser> decider) {
            super(name, context, decider);
        }
    }
}
```

and then use the feature flag as the marker name and target the beta testers group:

```java
public class LDMarkerTest {
  private static LDClientInterface client;

  @BeforeAll
  public static void setUp() {
      client = new LDClient("sdk-key");
  }

  @AfterAll
  public static void shutDown() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testMatchingMarker() throws JoranException {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    LDMarkerFactory markerFactory = new LDMarkerFactory(client);
    LDUser ldUser = new LDUser.Builder("UNIQUE IDENTIFIER")
            .firstName("Bob")
            .lastName("Loblaw")
            .customString("groups", singletonList("beta_testers"))
            .build();

    LDMarkerFactory.LDMarker ldMarker = markerFactory.create("turbomarker", ldUser);

    logger.info(ldMarker, "Hello world, I am info");
    logger.debug(ldMarker, "Hello world, I am debug");

    ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
    assertThat(appender.list.size()).isEqualTo(2);

    appender.list.clear();
  }
}
```

This is also a reason why [diagnostic logging is better than a debugger](https://lemire.me/blog/2016/06/21/i-do-not-use-a-debugger/).  Debuggers are ephemeral, can't be used in production, and don't produce a consistent record of events: debugging log statements are the single best way to dump internal state and manage code flows in an application.

## Tap Filters

A tap filter is used to tap some amount of incoming process and pass them to a specially configured appender even if they do not qualify as a logging event under normal circumstances.
 
This is a <a href="https://www.enterpriseintegrationpatterns.com/patterns/messaging/WireTap.html">wiretap</a> pattern from Enterprise Integration Patterns.
 
Tap Filters are very useful as a way to send data to an appender.  They completely bypass any kind of logging level configured on the front end, so you can set a logger to INFO level but still have access to all TRACE events when an error occurs, through the tap filter's appenders.

For example, a tap filter can automatically log everything with a correlation id at a TRACE level, without requiring filters or altering the log level as a whole.

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

This is only one approach to storing diagnostic information -- the other approach is to use turbo filters and markers based on ring buffers.

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

## Instrumenting Compiled Code with Logging using Byte Buddy

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives roughly the same performance as inline code, adding [130 ns/op](https://github.com/raphw/byte-buddy/issues/714) by calling `class.getMethod`.

There are two ways you can instrument code.  The first way is to do it in process, after the JVM has loaded.  The second way is to load the java agent before the JVM starts, which lets you instrument classes on the system classloader.

### Instrumenting Application Code

The in process instrumentation is done with `com.tersesystems.logback.bytebuddy.LoggingInstrumentationByteBuddyBuilder`, which takes in some configuration and then installs itself on the byte buddy agent.

```java
new LoggingInstrumentationByteBuddyBuilder()
        .builderFromConfig(loggingInstrumentationAdviceConfig)
        .with(debugListener)
        .installOnByteBuddyAgent();
```

This is driven from configuration, so with the following code:

```java
public class ClassCalledByAgent {
    public void printStatement() {
        System.out.println("I am a simple println method with no logging");
    }

    public void printArgument(String arg) {
        System.out.println("I am a simple println, printing " + arg);
    }

    public void throwException(String arg) {
        throw new RuntimeException("I'm a squirrel!");
    }
}
```

And the following configuration in `logback.conf`:

```hocon
logback.bytebuddy {
  service-name = "example-service"
  tracing {
    "com.tersesystems.logback.bytebuddy.ClassCalledByAgent" = [
      "printStatement",
      "printArgument",
      "throwException",
    ]
  }
}
```

and have `com.tersesystems.logback.bytebuddy.ClassCalledByAgent` logging level set to `TRACE` in `logback.xml`.

We can start up the agent, add in the builder and run through the methods:

```java
public class InProcessInstrumentationExample {

    public static AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return new AgentBuilder.Listener.Filtering(
                LoggingInstrumentationAdvice.stringMatcher(classNames),
                AgentBuilder.Listener.StreamWriting.toSystemOut());
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        Logger logger = LoggerFactory.getLogger(InProcessInstrumentationExample.class);
        SystemFlow.setLoggerResolver(new FixedLoggerResolver(logger));

        Config config = LoggingInstrumentationAdvice.generateConfig(ClassLoader.getSystemClassLoader(), false);
        LoggingInstrumentationAdviceConfig adviceConfig = LoggingInstrumentationAdvice.generateAdviceConfig(config);

        // The debugging listener shows what classes are being picked up by the instrumentation
        Listener debugListener = createDebugListener(adviceConfig.classNames());
        new LoggingInstrumentationByteBuddyBuilder()
                .builderFromConfig(adviceConfig)
                .with(debugListener)
                .installOnByteBuddyAgent();

        // No code change necessary here, you can wrap completely in the agent...
        ClassCalledByAgent classCalledByAgent = new ClassCalledByAgent();
        classCalledByAgent.printStatement();
        classCalledByAgent.printArgument("42");

        try {
            classCalledByAgent.throwException("hello world");
        } catch (Exception e) {
            // I am too lazy to catch this exception.  I hope someone does it for me.
        }
    }
}
```

And get the following:

```text
[Byte Buddy] DISCOVERY com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
[Byte Buddy] TRANSFORM com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
[Byte Buddy] COMPLETE com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
524   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printStatement() with arguments=[] from source ClassCalledByAgent.java:18
I am a simple println method with no logging
529   TRACE c.t.l.b.InProcessInstrumentationExample - exiting: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printStatement() with arguments=[] => returnType=void from source ClassCalledByAgent.java:19
529   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printArgument(java.lang.String) with arguments=[42] from source ClassCalledByAgent.java:22
I am a simple println, printing 42
529   TRACE c.t.l.b.InProcessInstrumentationExample - exiting: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printArgument(java.lang.String) with arguments=[42] => returnType=void from source ClassCalledByAgent.java:23
529   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(java.lang.String) with arguments=[hello world] from source ClassCalledByAgent.java:26
532   ERROR c.t.l.b.InProcessInstrumentationExample - throwing: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(java.lang.String) with arguments=[hello world] ! thrown=java.lang.RuntimeException: I'm a squirrel!
java.lang.RuntimeException: I'm a squirrel!
	at com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(ClassCalledByAgent.java:26)
	at com.tersesystems.logback.bytebuddy.InProcessInstrumentationExample.main(InProcessInstrumentationExample.java:65)
```

The `[Byte Buddy]` statements up top are caused by the debug listener, and let you know that Byte Buddy has successfully instrumented the class.  Note also that there is no runtime overhead in pulling line numbers or source files into the enter/exit methods, as these are pulled directly from bytecode and do not involve `fillInStackTrace`.

If you want to trace all the methods in a class, you can use a wildcard, i.e. `"mypackage.MyClass" = ["*"]`.

If you are using `logback-typesafe-config` then you can also set the levels from `logback.conf`, which can be very convenient.  For example in a Play application, you could set:

```hocon
levels {
  controllers = TRACE
  services = TRACE
  models = TRACE
  play.api.mvc = TRACE
}

logback.bytebuddy {
  service-name = "play-hello-world"

  tracing {
    "play.api.mvc.ActionBuilder" = ["*"]
    "play.api.mvc.BodyParser" = ["*"]
    "play.api.mvc.ActionFunction" = ["*"]

    "services.ComputerService" = ["*"]
    "controllers.HomeController" = ["*"]
    "models.Computer" = ["*"]
  }
}
```

and get automatic tracing.  This works particularly well with the honeycomb appender.

### Instrumenting System Classes

Instrumenting system level classes is a bit more involved, but can be done in configuration.

> **NOTE**: There are some limitations to instrumenting system level code.  You cannot instrument native methods like `java.lang.System.currentTimeMillis()` for example.

First, you set the java agent, either directly on the command line:

```bash
java \
  -javaagent:path/to/logback-bytebuddy-x.x.x.jar=debug \
  -Dterse.logback.configurationFile=conf/logback.conf \
  -Dlogback.configurationFile=conf/logback-test.xml \
  com.example.PreloadedInstrumentationExample
```

or by using the [`JAVA_TOOLS_OPTIONS` environment variable](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/envvars002.html).

```bash
export JAVA_TOOLS_OPTIONS="..."
```

and then in `logback.conf`:

```hocon
levels {
  java.lang.Thread = TRACE
}

logback.bytebuddy {
  service-name = "some-service"
  tracing {
    "java.lang.Thread" = [
      "run"
    ]
  }
}
```

and the code as follows:

```java
public class PreloadedInstrumentationExample {
    public static void main(String[] args) throws Exception {
        Thread thread = Thread.currentThread();
        thread.run();
    }
}
```

yields

```text
[Byte Buddy] DISCOVERY java.lang.Thread [null, null, loaded=true]
[Byte Buddy] TRANSFORM java.lang.Thread [null, null, loaded=true]
[Byte Buddy] COMPLETE java.lang.Thread [null, null, loaded=true]
92    TRACE java.lang.Thread - entering: java.lang.Thread.run() with arguments=[]
93    TRACE java.lang.Thread - exiting: java.lang.Thread.run() with arguments=[] => returnType=void
```

This is especially helpful when you're trying to debug SSL issues:

```hocon
levels {
  sun.security.ssl = TRACE
  javax.net.ssl = TRACE
}

logback.bytebuddy {
  service-name = "some-service"
  tracing {  
    "javax.net.ssl.SSLContext" = ["*"]
  }
}
```

will result in:

```
FcJ3XfsdKnM6O0Qbm7EAAA 12:31:55.498 [TRACE] j.n.s.SSLContext -  entering: javax.net.ssl.SSLContext.getInstance(java.lang.String) with arguments=[TLS] from source SSLContext.java:155
FcJ3XfsdKng6O0Qbm7EAAA 12:31:55.503 [TRACE] j.n.s.SSLContext -  exiting: javax.net.ssl.SSLContext.getInstance(java.lang.String) with arguments=[TLS] => returnType=javax.net.ssl.SSLContext from source SSLContext.java:157
FcJ3XfsdKng6O0Qbm7EAAB 12:31:55.504 [TRACE] j.n.s.SSLContext -  entering: javax.net.ssl.SSLContext.init([Ljavax.net.ssl.KeyManager;,[Ljavax.net.ssl.TrustManager;,java.security.SecureRandom) with arguments=[[org.postgresql.ssl.LazyKeyManager@27a97e08], [org.postgresql.ssl.NonValidatingFactory$NonValidatingTM@5918c260], null] from source SSLContext.java:282
FcJ3XfsdKnk6O0Qbm7EAAA 12:31:55.504 [TRACE] j.n.s.SSLContext -  exiting: javax.net.ssl.SSLContext.init([Ljavax.net.ssl.KeyManager;,[Ljavax.net.ssl.TrustManager;,java.security.SecureRandom) with arguments=[[org.postgresql.ssl.LazyKeyManager@27a97e08], [org.postgresql.ssl.NonValidatingFactory$NonValidatingTM@5918c260], null] => returnType=void from source SSLContext.java:283
```

Be warned that JSSE can be extremely verbose in its `toString` output.

## Censoring Sensitive Information

There may be sensitive information that you don't want to show up in the logs.  You can get around this by passing your information through a censor.  This is a custom bit of code written for Logback, but it's not too complex.

There are two rules and a converter that are used in Logback to define and reference censors: `CensorAction`, `CensorRefAction` and the `censor` converter.

```xml
<configuration>
    <newRule pattern="*/censor"
             actionClass="com.tersesystems.logback.censor.CensorAction"/>

    <newRule pattern="*/censor-ref"
             actionClass="com.tersesystems.logback.censor.CensorRefAction"/>

    <conversionRule conversionWord="censor" converterClass="com.tersesystems.logback.censor.CensorConverter" />

    <!-- ... -->
</configuration>
```

The `CensorAction` defines a censor that can be referred to by the `CensorRef` action and the `censor` conversionWord, using the censor name.  The default implementation is the regex censor, which will look for a regular expression and replace it with the replacement text defined:

```xml
<configuration>
    <censor name="censor-name1" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR1]</replacementText>
        <regex>hunter1</regex>
    </censor>

    <censor name="censor-name2" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR2]</replacementText>
        <regex>hunter2</regex>
    </censor>
</configuration>
```

Once you have the censors defined, you can use the censor word by specifying the target as defined in the [pattern encoder format](https://logback.qos.ch/manual/layouts.html#conversionWord), and adding the name as the option list using curly braces, i.e. `%censor(%msg){censor-name1}`.  If you don't define the censor, then the first available censor will be picked.

```xml
<configuration>
    <appender name="TEST1" class="ch.qos.logback.core.FileAppender">
        <file>file1.log</file>
        <encoder>
            <pattern>%censor(%msg){censor-name1}%n</pattern>
        </encoder>
    </appender>

    <appender name="TEST2" class="ch.qos.logback.core.FileAppender">
        <file>file2.log</file>
        <encoder>
            <pattern>%censor(%msg){censor-name2}%n</pattern>
        </encoder>
    </appender>
</configuration>
```

If you are working with a componentized framework, you'll want to use the `censor-ref` action instead.  Here's an example using logstash-logback-encoder.

```xml
<configuration>
    <appender name="TEST3" class="ch.qos.logback.core.FileAppender">
        <file>file3.log</file>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator">
                <censor-ref ref="json-censor"/>
            </jsonGeneratorDecorator>
        </encoder>
    </appender>
</configuration>
```

In this case, `CensoringJsonGeneratorDecorator` implements the `CensorAttachable` interface and so will run message text through the censor if it exists.

## Exception Mapping

Exception Mapping is done to show the important details of an exception, including the root cause in a summary format.  This is especially useful in line oriented formats, because rendering a stacktrace can take up screen real estate without providing much value.

An example will help.  Given the following program:

```java
public class Thrower {
    private static final Logger logger = LoggerFactory.getLogger(Thrower.class);

    public static void main(String[] progArgs) {
        try {
            doSomethingExceptional();
        } catch (RuntimeException e) {
            logger.error("domain specific message", e);
        }
    }

    static void doSomethingExceptional() {
        Throwable cause = new BatchUpdateException();
        throw new MyCustomException("This is my message", "one is one", "two is more than one", "three is more than two and one", cause);
    }
}

public class MyCustomException extends RuntimeException {
    public MyCustomException(String message, String one, String two, String three, Throwable cause) {
       // ...
    }
    public String getOne() { return one; }
    public String getTwo() { return two; }
    public String getThree() { return three; }
}
```

and the Logback file:

```xml
<configuration>

  <newRule pattern="*/exceptionMappings"
           actionClass="com.tersesystems.logback.exceptionmapping.ExceptionMappingRegistryAction"/>

  <newRule pattern="*/exceptionMappings/mapping"
           actionClass="com.tersesystems.logback.exceptionmapping.ExceptionMappingAction"/>

  <conversionRule conversionWord="richex" converterClass="com.tersesystems.logback.exceptionmapping.ExceptionMessageWithMappingsConverter" />

  <exceptionMappings>
    <!-- comes with default mappings for JDK exceptions, but you can add your own -->
    <mapping name="com.tersesystems.logback.exceptionmapping.MyCustomException" properties="one,two,three"/>
  </exceptionMappings>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%-5relative %-5level %logger{35} - %msg%richex{1, 10, exception=[}%n</pattern>
    </encoder>
  </appender>

  <root level="TRACE">
    <appender-ref ref="CONSOLE"/>
  </root>

</configuration>
```

Then this renders the following:

```
184   ERROR c.t.l.exceptionmapping.Thrower - domain specific message exception=[com.tersesystems.logback.exceptionmapping.MyCustomException(one="one is one" two="two is more than one" three="three is more than two and one" message="This is my message") > java.sql.BatchUpdateException(updateCounts="null" errorCode="0" SQLState="null" message="null")]
```

You can integrate exception mapping with Typesafe Config and `logstash-logback-encoder` by adding extra mappings.

For example, you can map a whole bunch of exceptions at once in HOCON, and not have to do it line by line in XML:

```xml
<configuration>
  <newRule pattern="*/exceptionMappings/configMappings"
           actionClass="com.tersesystems.logback.exceptionmapping.config.TypesafeConfigMappingsAction"/>

  <exceptionMappings>
    <!-- Or point to HOCON path -->
    <configMappings path="exceptionmappings"/>
  </exceptionMappings>
</configuration>
```

and

```hocon
exceptionmappings {
   example.MySpecialException: ["timestamp"]
}
```

and configure it in JSON using `ExceptionArgumentsProvider`:

```xml
<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
  <providers>
    <provider class="com.tersesystems.logback.exceptionmapping.json.ExceptionArgumentsProvider">
      <fieldName>exception</fieldName>
    </provider>
  </providers>
</encoder>
```

and get the following `exception` that contains an array of exceptions and the associated properties, in this case `timestamp`:

```json
{
  "id" : "Fa6x8H0EqomdHaINzdiAAA",
  "sequence" : 3,
  "@timestamp" : "2019-07-06T03:52:48.730+00:00",
  "@version" : "1",
  "message" : "I am an error",
  "logger_name" : "example.Main$Runner",
  "thread_name" : "pool-1-thread-1",
  "level" : "ERROR",
  "stack_hash" : "233f3cf1",
  "exception" : [ {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 1",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 2",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 3",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 4",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 5",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 6",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 7",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 8",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 9",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  } ],
  "stack_trace" : "<#1165e3b1> example.MySpecialException: Level 9\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 9 common frames omitted\nWrapped by: <#eb336a2d> example.MySpecialException: Level 8\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 10 common frames omitted\nWrapped by: <#cc1fb404> example.MySpecialException: Level 7\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 11 common frames omitted\nWrapped by: <#2af187a0> example.MySpecialException: Level 6\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 12 common frames omitted\nWrapped by: <#7dac62d1> example.MySpecialException: Level 5\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 13 common frames omitted\nWrapped by: <#2ea4460d> example.MySpecialException: Level 4\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 14 common frames omitted\nWrapped by: <#261bed64> example.MySpecialException: Level 3\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 15 common frames omitted\nWrapped by: <#e660d440> example.MySpecialException: Level 2\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 16 common frames omitted\nWrapped by: <#233f3cf1> example.MySpecialException: Level 1\n\tat example.Main$Runner.nestException(Main.java:56)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.generateException(Main.java:51)\n\tat example.Main$Runner.doError(Main.java:44)\n\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n\tat java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)\n\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)\n\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\n"
}
```

This is a lot easier for structured logging parsers to grok than the associated stacktrace.

## Exposing System Information with Sigar

In [What is Happening: Attempting to Understand Our Systems](https://www.youtube.com/watch?v=xy3w2hGijhE), there's [a slide](https://speakerdeck.com/lyddonb/what-is-happening-attempting-to-understand-our-systems?slide=133) that suggests the following information should always be available as telemetry data:

> The user (and/or company), time, machine stats (CPU, Memory, etc), software version, configuration data, the calling request, any dependent requests

The interesting bit here is the machine stats, such as CPU and memory, and how they relate to Logback.  Machine status can be very relevant when it comes to resource failures, and providing a detailed view of CPU and memory tied to logs is an interesting concept.

There's a tool, [Hyperic Sigar](https://github.com/hyperic/sigar), which is very good at exposing system metrics. 
 
Using the `logback-sigar` module, it's relatively easy to add Sigar into context using `com.tersesystems.logback.sigar.SigarAction`:

```xml
<configuration>
  <newRule pattern="*/sigar" actionClass="com.tersesystems.logback.sigar.SigarAction"/>

  <sigar/>

  <conversionRule conversionWord="cpu" converterClass="com.tersesystems.logback.sigar.CPUPercentageConverter"/>
  <conversionRule conversionWord="mem" converterClass="com.tersesystems.logback.sigar.MemoryPercentageConverter"/>
  <conversionRule conversionWord="loadavg" converterClass="com.tersesystems.logback.sigar.LoadAverageConverter"/>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>[%-5level] %logger{15} %cpu %mem %loadavg - %msg%n%xException{10}</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="CONSOLE" />
  </root>
</configuration>
```

And then render the CPU, memory and load average as follows:

```text
[ERROR] c.example.Test sys=0.007594936708860759 user=0.10379746835443038 used=9269886976 used%=24.923867415973078 total=25064484864 load1min=2.18 load5min=1.5 load15min=1.07 - I am very much under load
```

Note that if you want to integrate this with Logstash `StructuredArgument` or `Markers` then you'll want to make your component implement `SigarContextAware` and then query appropriately.  There are some very fun things you can do with Sigar like add [Process Table Query Language](https://shervinasgari.blogspot.com/2011/03/api-helper-wrapper-for-processfinder-in.html) together with some feature flag stuff to do dynamic queries into the machine.

## Budget Aware Logging

There are instances where loggers may be overly chatty, and will log more than necessary.  Rather than hunt down all the individual loggers and whitelist or blacklist the lot of them, you may want to assign a budget that will budget INFO messages to 5 statements a second.

This is easy to do with the `logback-budget` module, which uses an internal [circuit breaker](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/concurrent/CircuitBreaker.html) to regulate the flow of messages.

```xml
<configuration>
    <!-- <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />-->

    <newRule pattern="*/budget-rule"
             actionClass="com.tersesystems.logback.budget.BudgetRuleAction"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
              <budget-rule name="INFO" threshold="5" interval="1" timeUnit="seconds"/>
            </evaluator>
            <OnMismatch>DENY</OnMismatch>
            <OnMatch>NEUTRAL</OnMatch>
        </filter>

        <encoder>
            <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="TRACE">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```

## Logback Specific Things

This section deals with the specific configuration in `terse-logback/logback-structured-config`.

Logback doesn't come with a default `logback.xml` file, and the [configuration page](https://logback.qos.ch/manual/configuration.html#auto_configuration) is written at a very low level that is not very useful for people.  The example has been written so that it doesn't "overwhelm" with too much detail, but in rough order of initialization:

* Logback XML with Custom Actions
* Loading Typesafe Config
* Log Levels and Properties through Typesafe Config
* High Performance Async Appenders
* Sensible Joran (Logback XML) Configuration

### Logback XML with Custom Actions

The entry point of the system is a `logback.xml` file which has custom actions added to it to do additional configuration, `TypesafeConfigAction` and `SetLoggerLevelsAction`.

This approach is not as fancy as using a service loader pattern, but there are issues integrating into web frameworks, as those frameworks may look directly for XML files and skip service loader patterns.  Using a `logback.xml` file is the most well known pattern, and Joran makes adding custom actions fairly easy.

### Loading Typesafe Config

The `TypesafeConfigAction` will search in a variety of places for configuration using [standard fallback behavior](https://github.com/lightbend/config#standard-behavior) for Typesafe Config, which gives a richer experience to end users.

```java
Config config = systemProperties        // Look for a property from system properties first...
        .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
        .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
        .withFallback(resources)     // then look in logback.conf...
        .withFallback(reference)     // and then finally in logback-reference.conf.
        .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.
```

The configuration is then placed in the `LoggerContext` which is available to all of Logback.

```java
lc.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);
```

And then all properties are made available to Logback, either at the `local` scope or at the `context` scope.

Properties must be strings, but you can also provide Maps and Lists to the Logback Context, through `context.getObject`.

### Log Levels and Properties through Typesafe Config

Configuration of properties and setting log levels is done through [Typesafe Config](https://github.com/lightbend/config#overview), using `TypesafeConfigAction`

Here's the `logback.conf` from the example application.  It's in Human-Optimized Config Object Notation or [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md).

```hocon
# Set logger levels here.
levels = {
    # Override the default root log level with ROOT_LOG_LEVEL environment variable, if defined...
    ROOT = ${?ROOT_LOG_LEVEL}

    # You can set a logger with a simple package name.
    example = DEBUG

    # You can also do nested overrides here.
    deeply.nested {
        package = TRACE
    }
}

# Overrides the properties from logback-reference.conf
local {

    logback.environment=production

    censor {
        regex = """hunter2""" // http://bash.org/?244321
        replacementText = "*******"
        json.keys += "password" // adding password key will remove the key/value pair entirely
    }

    # Overwrite text file on every run.
    textfile {
        append = false
    }

    # Override the color code in console for info statements
    highlight {
        info = "black"
    }
}

# You can also include settings from other places
include "myothersettings"
```

For tests, there's a `logback-test.conf` that will override (rather than completely replace) any settings that you have in `logback.conf`:

```hocon
include "logback-reference"

levels {
  example = TRACE
}

local {
  logback.environment=test

  textfile {
    location = "log/test/application-test.log"
    append = false
  }

  jsonfile {
    location = "log/test/application-test.json"
    prettyprint = true
  }
}
```

There is also a `logback-reference.conf` file that handles the default configuration for the appenders, and those settings can be overridden.  They are written out individually in the encoder configuration so I won't go over it here.

Note that appender logic is not available here -- it's all defined through the `structured-config` in `logback.xml`.

Using Typesafe Config is not a requirement -- the point here is to show that there are more options to configuring Logback than using a straight XML file.

### High Performance Async Appenders

The JSON and Text file appenders are wrapped in [LMAX Disruptor async appenders](https://github.com/logstash/logstash-logback-encoder#async-appenders).

This example comes preconfigured with a [shutdown hook](https://logback.qos.ch/manual/configuration.html#stopContext) to ensure the async appenders empty their queues before the application shuts down.

To my knowledge, the logstash async appenders have not been benchmarked against Log4J2, but async logging is ridiculously performant, and [will never be the bottleneck in your application](https://www.sitepoint.com/which-java-logging-framework-has-the-best-performance/#conclusions).

In general, you should only be concerned about the latency or throughput of your logging framework when you have sat down and done the math on how much logging it would take to stress out the system, asked about your operational requirements, and determined the operational costs, including IO and [rate limits](https://segment.com/blog/bob-loblaws-log-blog/#the-case-of-the-missing-logs), and a budget for logging.  Logging doesn't come for free.

### Sensible Joran (Logback XML) Configuration

The [XML configuration](https://logback.qos.ch/manual/configuration.html#syntax) for the main file is in `terse-logback.xml` and is as follows:

The `UniqueIdEventAppender` is an appender that decorates `ILoggingEvent` with a unique id that can be used to correlate the same log entry across different appenders.

```xml
<configuration>

    <include resource="terse-logback/initial.xml"/>
    <include resource="terse-logback/censor.xml"/>

    <include resource="terse-logback/appenders/audio-appenders.xml"/>
    <include resource="terse-logback/appenders/console-appenders.xml"/>
    <include resource="terse-logback/appenders/jsonfile-appenders.xml"/>
    <include resource="terse-logback/appenders/textfile-appenders.xml"/>

    <appender name="development" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="AUDIO"/>
        <appender-ref ref="ASYNC_TEXTFILE"/>
        <appender-ref ref="ASYNC_JSONFILE"/>
    </appender>

    <appender name="test" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="ASYNC_TEXTFILE"/>
    </appender>

    <appender name="production" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_JSONFILE"/>
    </appender>

    <appender name="selector" class="com.tersesystems.logback.core.SelectAppender">
        <!-- Set logback.conf or logback-test.conf with "local.logback.environment=development" -->
        <appenderKey>${logback.environment}</appenderKey>

        <appender-ref ref="development"/>
        <appender-ref ref="production"/>
        <appender-ref ref="test"/>
    </appender>

    <appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdEventAppender">
        <appender-ref ref="selector"/>
    </appender>

    <root>
        <appender-ref ref="selector-with-unique-id"/>
    </root>

    <include resource="terse-logback/ending.xml" />
</configuration>
```

All the encoders have been configured to use UTC as the timezone, and are packaged individually using [file inclusion](https://logback.qos.ch/manual/configuration.html#fileInclusion) for ease of use.

#### Console

The console appender uses the following XML configuration:

```xml
<included>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="com.tersesystems.logback.EnabledFilter">
            <enabled>${console.enabled}</enabled>
        </filter>
        <encoder>
            <pattern>${console.encoder.pattern}</pattern>
        </encoder>
        <withJansi>${console.withJansi}</withJansi>
    </appender>
</included>
```

with the HOCON settings as follows:

```hocon
  console {
    enabled = true
    withJansi = true # allow colored logging on windows
    encoder {
      pattern = "[%terseHighlight(%-5level)] %logger{15} -  %censor(%message){text-censor}%n%xException{10}"
    }
  }
```

The console appender uses colored logging for the log level, but you can override config to set the colors you want for which levels.  Jansi is included so that Windows can benefit from colored logging as well.

The console does not use async logging, because it has to co-exist with `System.out.println` and `System.err.println` messages, and so must appear time-ordered with them.

#### Text

The text encoder uses the following configuration:

```xml
<included>
    <appender name="TEXTFILE" class="ch.qos.logback.core.FileAppender">
        <filter class="com.tersesystems.logback.EnabledFilter">
            <enabled>${textfile.enabled}</enabled>
        </filter>
        <file>${textfile.location}</file>
        <append>${textfile.append}</append>

        <!--
          This quadruples logging throughput (in theory) https://logback.qos.ch/manual/appenders.html#FileAppender
         -->
        <immediateFlush>${textfile.immediateFlush}</immediateFlush>

        <encoder>
            <pattern>${textfile.encoder.pattern}</pattern>
            <outputPatternAsHeader>${textfile.encoder.outputPatternAsHeader}</outputPatternAsHeader>
        </encoder>
    </appender>

    <!--
      https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#async-appenders
    -->
    <appender name="ASYNCTEXTFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="TEXTFILE" />
    </appender>
</included>
```

with the HOCON settings as:

```hocon
  // used in textfile-appenders.xml
  textfile {
    enabled = true
    location = ${properties.log.dir}/application.log
    append = true
    immediateFlush = true

    rollingPolicy {
      fileNamePattern = ${properties.log.dir}"/application.log.%d{yyyy-MM-dd}"
      maxHistory = 30
    }

    encoder {
      outputPatternAsHeader = true

      // https://github.com/logstash/logstash-logback-encoder/blob/master/src/main/java/net/logstash/logback/stacktrace/ShortenedThrowableConverter.java#L58
      // Options can be specified in the pattern in the following order:
      //   - maxDepthPerThrowable = "full" or "short" or an integer value
      //   - shortenedClassNameLength = "full" or "short" or an integer value
      //   - maxLength = "full" or "short" or an integer value
      //
      //%msg%n%stack{5,1024,10,rootFirst,regex1,regex2,evaluatorName}

      pattern = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %censor(%message){text-censor}%n%stack{full,full,short,rootFirst}"
    }
  }
```

Colored logging is not used in the file-based appender, because some editors tend to show ANSI codes specifically.

#### JSON

The JSON encoder uses [`net.logstash.logback.encoder.LogstashEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#encoders--layouts) with pretty print options.

The XML is as follows:

```xml
<included>
    <appender name="ASYNC_JSONFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <filter class="com.tersesystems.logback.core.EnabledFilter">
            <enabled>${jsonfile.enabled}</enabled>
        </filter>
        <appender class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>${jsonfile.location}</file>
            <append>${jsonfile.append}</append>

            <!--
              This quadruples logging throughput (in theory) https://logback.qos.ch/manual/appenders.html#FileAppender
             -->
            <immediateFlush>${jsonfile.immediateFlush}</immediateFlush>

            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>${jsonfile.rollingPolicy.fileNamePattern}</fileNamePattern>
                <maxHistory>${jsonfile.rollingPolicy.maxHistory}</maxHistory>
            </rollingPolicy>

            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <pattern>
                        <pattern>
                            { "id": "%uniqueId" }
                        </pattern>
                    </pattern>
                    <sequence/>
                    <timestamp>
                        <!-- UTC is the best server consistent timezone -->
                        <timeZone>${jsonfile.encoder.timeZone}</timeZone>
                        <timestampPattern>${jsonfile.encoder.timestampPattern}</timestampPattern>
                    </timestamp>
                    <version/>
                    <message/>
                    <loggerName/>
                    <threadName/>
                    <logLevel/>
                    <stackHash/>
                    <mdc/>
                    <logstashMarkers/>
                    <arguments/>

                    <provider class="com.tersesystems.logback.exceptionmapping.json.ExceptionArgumentsProvider">
                        <fieldName>exception</fieldName>
                    </provider>

                    <stackTrace>
                        <!--
                          https://github.com/logstash/logstash-logback-encoder#customizing-stack-traces
                        -->
                        <throwableConverter class="net.logstash.logback.stacktrace.ShortenedThrowableConverter">
                            <rootCauseFirst>${jsonfile.shortenedThrowableConverter.rootCauseFirst}</rootCauseFirst>
                            <inlineHash>${jsonfile.shortenedThrowableConverter.inlineHash}</inlineHash>
                        </throwableConverter>
                    </stackTrace>
                </providers>

                <!-- https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#customizing-json-factory-and-generator -->
                <!-- XXX it would be much nicer to use OGNL rather than Janino, but out of scope... -->
                <if condition='p("jsonfile.prettyprint").contains("true")'>
                    <then>
                        <!-- Pretty print for better end user experience. -->
                        <jsonGeneratorDecorator
                                class="com.tersesystems.logback.censor.CensoringPrettyPrintingJsonGeneratorDecorator">
                            <censor-ref ref="json-censor"/>
                        </jsonGeneratorDecorator>
                    </then>
                    <else>
                        <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator">
                            <censor-ref ref="json-censor"/>
                        </jsonGeneratorDecorator>
                    </else>
                </if>
            </encoder>
        </appender>
    </appender>
</included>
```

with the following HOCON configuration:

```hocon
  // Used in jsonfile-appenders.xml
  jsonfile {
    enabled = true
    location = ${properties.log.dir}"/application.json"
    append = true
    immediateFlush = true
    prettyprint = false

    rollingPolicy {
      fileNamePattern = ${properties.log.dir}"/application.json.%d{yyyy-MM-dd}"
      maxHistory = 30
    }

    encoder {
      includeContext = false
      timeZone = "UTC"
    }

    # https://github.com/logstash/logstash-logback-encoder#customizing-stack-traces
    shortenedThrowableConverter {
      maxDepthPerThrowable = 100
      maxLength = 100
      shortenedClassNameLength = 50

      exclusions = """\$\$FastClassByCGLIB\$\$,\$\$EnhancerBySpringCGLIB\$\$,^sun\.reflect\..*\.invoke,^com\.sun\.,^sun\.net\.,^net\.sf\.cglib\.proxy\.MethodProxy\.invoke,^org\.springframework\.cglib\.,^org\.springframework\.transaction\.,^org\.springframework\.validation\.,^org\.springframework\.app\.,^org\.springframework\.aop\.,^java\.lang\.reflect\.Method\.invoke,^org\.springframework\.ws\..*\.invoke,^org\.springframework\.ws\.transport\.,^org\.springframework\.ws\.soap\.saaj\.SaajSoapMessage\.,^org\.springframework\.ws\.client\.core\.WebServiceTemplate\.,^org\.springframework\.web\.filter\.,^org\.apache\.tomcat\.,^org\.apache\.catalina\.,^org\.apache\.coyote\.,^java\.util\.concurrent\.ThreadPoolExecutor\.runWorker,^java\.lang\.Thread\.run$"""

      rootCauseFirst = true
      inlineHash = true
    }
  }
```

If you want to modify the format of the JSON encoder, you should use [`LoggingEventCompositeJsonEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#composite-encoderlayout).  The level of detail in `LoggingEventCompositeJsonEncoder` is truly astounding and it's a powerful piece of work in its own right.

## Audio

The audio appender uses a system beep configured through `SystemPlayer` to notify on warnings and errors, and limits excessive beeps with a budget evaluator.

The XML is as follows:

```xml
<included>

    <appender name="AUDIO-WARN" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>NEUTRAL</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO-ERROR" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO" class="com.tersesystems.logback.core.CompositeAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
                <budgetRule name="WARN" threshold="1" interval="5" timeUnit="seconds"/>
                <budgetRule name="ERROR" threshold="1" interval="5" timeUnit="seconds"/>
            </evaluator>
            <OnMismatch>DENY</OnMismatch>
            <OnMatch>NEUTRAL</OnMatch>
        </filter>

        <appender-ref ref="AUDIO-WARN"/>
        <appender-ref ref="AUDIO-ERROR"/>
    </appender>

</included>
```

## Further Reading

### Logback Encoders and Appenders

* [concurrent-build-logger](https://github.com/takari/concurrent-build-logger) (encoders and appenders both)
* [logzio-logback-appender](https://github.com/logzio/logzio-logback-appender)
* [logback-elasticsearch-appender](https://github.com/internetitem/logback-elasticsearch-appender)
* [logback-more-appenders](https://github.com/sndyuk/logback-more-appenders)
* [logback-steno](https://github.com/ArpNetworking/logback-steno)
* [logslack](https://github.com/gmethvin/logslack)
* [Lessons Learned Writing New Logback Appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/)
* [Extending logstash-logback-encoder](https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/)

### Best Practices

Many of these are logback specific, but still good overall.

* [9 Logging Best Practices Based on Hands-on Experience](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [Woofer: logging in (best) practices](https://orange-opensource.github.io/woofer/logging-code/): Spring Boot
* [A whole product concern logging implementation](http://stevetarver.github.io/2016/04/20/whole-product-logging.html)
* [There is more to logging than meets the eye](https://allegro.tech/2015/10/there-is-more-to-logging-than-meets-the-eye.html)
* [Monitoring demystified: A guide for logging, tracing, metrics](https://techbeacon.com/enterprise-it/monitoring-demystified-guide-logging-tracing-metrics)

Stack Overflow has a couple of good tips on SLF4J and Logging:

* [When to use the different log levels](https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels)
* [Why does the TRACE level exist, and when should I use it rather than DEBUG?](https://softwareengineering.stackexchange.com/questions/279690/why-does-the-trace-level-exist-and-when-should-i-use-it-rather-than-debug)
* [Best practices for using Markers in SLF4J/Logback](https://stackoverflow.com/questions/4165558/best-practices-for-using-markers-in-slf4j-logback)
* [Stackoverflow: Logging best practices in multi-node environment](https://stackoverflow.com/questions/43496695/java-logging-best-practices-in-multi-node-environment)

#### Level Up Logs

[Alberto Navarro](https://looking4q.blogspot.com/) has a great series

<ol>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-introduction.html">Introduction</a> (Everyone)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-your-logs-and-elk-json-logs.html">JSON as logs format</a> (Everyone)</li>
<li><b><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-logging-best.html">Logging best practices with Logback</a> (Targetting Java DEVs)</b></li>
<li><a href="https://looking4q.blogspot.com/2018/11/logging-cutting-edge-practices.html">Logging cutting-edge practices</a> (Targetting Java DEVs)&nbsp;</li>
<li><a href="https://looking4q.blogspot.com/2019/01/level-up-logs-and-elk-contract-first.html">Contract first log generator</a> (Targetting Java DEVs) </li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-elasticsearch.html">ElasticSearch VRR Estimation Strategy</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-java-logback.html">VRR Java + Logback configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-filebeat.html">VRR FileBeat configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-logstash.html">VRR Logstash configuration and Index templates</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-curator.html">VRR Curator configuration</a> (Targetting OPS)</li>
<li><a href="https://looking4q.blogspot.com/2018/10/level-up-logs-and-elk-logstash-grok.html">Logstash Grok, JSON Filter and JSON Input performance comparison</a> (Targetting OPS) </li>
</ol>

#### Logging Anti Patterns

Logging Anti-Patterns by [Rolf Engelhard](https://rolf-engelhard.de/):

* [Logging Anti-Patterns](http://rolf-engelhard.de/2013/03/logging-anti-patterns-part-i/)
* [Logging Anti-Patterns, Part II](http://rolf-engelhard.de/2013/04/logging-anti-patterns-part-ii/)
* [Logging Anti-Patterns, Part III](https://rolf-engelhard.de/2013/10/logging-anti-patterns-part-iii/)

#### Clean Code, clean logs

[Tomasz Nurkiewicz](https://www.nurkiewicz.com/) has a great series on logging:

* [Clean code, clean logs: use appropriate tools (1/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-use-appropriate.html)
* [Clean code, clean logs: logging levels are there for you (2/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: do you know what you are logging? (3/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-do-you-know-what.html)
* [Clean code, clean logs: avoid side effects (4/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-avoid-side.html)
* [Clean code, clean logs: concise and descriptive (5/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-concise-and.html)
* [Clean code, clean logs: tune your pattern (6/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: log method arguments and return values (7/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-method.html)
* [Clean code, clean logs: watch out for external systems (8/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-watch-out-for.html)
* [Clean code, clean logs: log exceptions properly (9/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-exceptions.html)
* [Clean code, clean logs: easy to read, easy to parse (10/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-easy-to-read-easy.html)
* [Condensed 10 Tips on javacodegeeks](https://www.javacodegeeks.com/2011/01/10-tips-proper-application-logging.html)

## Release

To make sure everything works:

```bash
./gradlew clean build check
```

To format everything using [Spotless](https://github.com/diffplug/spotless/tree/master/plugin-gradle):

```bash
./gradlew spotlessApply
```

Releases are handled using [shipkit](https://github.com/mockito/shipkit):

```bash
./gradlew testRelease
```

And then to run the release, which will increment the version number in `version.properties`:

```bash
./gradlew performRelease
```
