# Tracing to Honeycomb

You can connect Logback to Honeycomb directly through the Honeycomb Logback appender, using the [Events API](https://docs.honeycomb.io/api/events/).  Posting data directly to Honeycomb lets you leverage Honeycomb's trace API to show logs as hierarchical traces and spans.  

Bear in mind that the tracing feature here is optional -- you can use the Honeycomb appender out of the box without tracing with just plain logs.

However, adding tracing through logging is interesting in a couple of different ways.  Using Honeycomb means logs can be immediately visualized and queried without setting up extensive infrastructure.  From a tracing perspective, it completely avoids the OpenTelemetry manual instrumentation usually needed for tracing, and allows for tweaks and customization without the sampling or collector assumptions involved.

## Implementation

Add the library dependency using [hhttps://mvnrepository.com/artifact/com.tersesystems.logback/logback-honeycomb-okhttp](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-honeycomb-okhttp) for the honeycomb appender.

To set up tracing, add [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-tracing](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-tracing) and [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-classic](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-classic) for the start time converter.

## Usage

The appender is of type `com.tersesystems.logback.honeycomb.HoneycombAppender`, and makes use of the client under the hood.  Because the honeycomb appender uses an HTTP client under the hood, there are a couple of important notes.

> **NOTE**: Because the HTTP client runs on a different thread, you should make sure you either shutdown Logback explicitly by calling `loggerContext.stop`, or use [shutdown hook](http://logback.qos.ch/manual/configuration.html#shutdownHook) configured so that shutting down can be delayed until the events are posted.

The appender is as follows:

```xml
<configuration>
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook">
    <delay>1000</delay>
  </shutdownHook>

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

The start time information is captured in a `StartTimeMarker` which can be extracted by `StartTime.from` and is used in building the Honeycomb Request.  The event timestamp serves as the span's end time.  This is useful in Honeycomb Tracing, as the timestamp is the start time, not the time that the log entry was posted.

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

See [Tracing With Logback and Honeycomb](https://tersesystems.com/blog/2019/08/22/tracing-with-logback-and-honeycomb/) and [Hierarchical Instrumented Tracing with Logback](https://tersesystems.com/blog/2019/09/15/hierarchical-instrumented-tracing-with-logback/) for more details.
