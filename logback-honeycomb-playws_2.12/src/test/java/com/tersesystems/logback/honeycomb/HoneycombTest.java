/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.honeycomb;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.tersesystems.logback.classic.Utils;
import com.tersesystems.logback.tracing.SpanInfo;
import com.tersesystems.logback.tracing.SpanMarkerFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.jupiter.api.Test;

public class HoneycombTest {

  public void notTestOutput() throws InterruptedException, JoranException {
    Utils utils = Utils.create("/logback-honeycomb-batch.xml");
    Logger logger = utils.getLogger("com.example.Test");

    // close the span and get the marker out.
    logger.info("Message one");
    logger.info("Message two");
    logger.info("Message three");
    logger.info("Message four");
    logger.info("Message five");
    logger.info("Message six");

    Thread.sleep(1000);

    List<Status> statusList = utils.getStatusList();
    statusList.forEach(System.out::println);
    List<Status> successes =
        statusList.stream()
            .filter(status -> status.getMessage().contains("postBatch: successful post"))
            .collect(Collectors.toList());

    assertThat(successes.size()).isEqualTo(3);
  }

  public void notTestMarker() throws JoranException, InterruptedException {
    Utils utils = Utils.create("/logback-honeycomb-event.xml");
    SpanMarkerFactory markerFactory = new SpanMarkerFactory();

    Logger logger = utils.getLogger("com.example.Test");

    // https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#manual-tracing
    String traceId = UUID.randomUUID().toString();
    SpanInfo method1Span = createRootSpan(traceId, "rootMethod", Instant.now().minusSeconds(3));
    LogstashMarker span1Marker = markerFactory.create(method1Span);

    SpanInfo method2Span =
        method1Span
            .childBuilder()
            .setName("childMethod")
            .setDurationSupplier(
                () -> Duration.between(Instant.now().minusSeconds(2), Instant.now()))
            .build();
    LogstashMarker span2Marker = markerFactory.create(method2Span);

    logger.info(span1Marker, "called first");
    logger.info(span2Marker, "called second");

    // Give the client time to post...
    Thread.sleep(1000);

    // dumpStatus(utils);

    StatusManager statusManager = utils.getLoggerContext().getStatusManager();
    List<Status> successes =
        statusManager.getCopyOfStatusList().stream()
            .filter(status -> status.getMessage().contains("postEvent: successful post"))
            .collect(Collectors.toList());

    assertThat(successes.size()).isEqualTo(2);
  }

  @Test
  public void testOnShutdown() {
    // On shutdown, the appender should dump the queue and send immediately
    // TODO add an assertion
  }

  // Get the status manager and look at the messages sent back.
  private void dumpStatus(Utils utils) {
    StatusManager statusManager = utils.getLoggerContext().getStatusManager();
    List<Status> copyOfStatusList = statusManager.getCopyOfStatusList();
    for (Status status : copyOfStatusList) {
      String message = status.getMessage();
      if (status.getLevel() == Status.ERROR) {
        System.err.println(message);
      } else {
        System.out.println(message);
      }
    }
  }

  private SpanInfo createRootSpan(String traceId, String methodName, Instant creationTime) {
    String serviceName = "sample_service";
    String spanId = UUID.randomUUID().toString();
    Supplier<Duration> durationSupplier = () -> Duration.between(creationTime, Instant.now());

    return SpanInfo.builder()
        .setName(methodName)
        .setSpanId(spanId)
        .setTraceId(traceId)
        .setStartTime(creationTime)
        .setServiceName(serviceName)
        .setDurationSupplier(durationSupplier)
        .build();
  }
}
