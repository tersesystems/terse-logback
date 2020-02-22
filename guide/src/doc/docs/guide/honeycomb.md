
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
