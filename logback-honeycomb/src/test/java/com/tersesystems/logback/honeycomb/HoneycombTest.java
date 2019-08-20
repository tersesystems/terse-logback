/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.honeycomb;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.tersesystems.logback.classic.Utils;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class HoneycombTest {

    @Test
    public void testOutput() throws InterruptedException {
        Utils utils = Utils.create();
        Logger logger = utils.getLogger("com.example.Test").get();

        // close the span and get the marker out.
        logger.info( "Message one");
        logger.info( "Message two");
        logger.info( "Message three");
        logger.info( "Message four");
        logger.info( "Message five");
        logger.info( "Message six");

        Thread.sleep(1000);
        dumpStatus();

        // TODO add an assertion
    }

    // Get the status manager and look at the messages sent back.
    private void dumpStatus() {
        Utils utils = Utils.create();
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

    @Test
    public void testMarker() {
        HoneycombMarkerFactory markerFactory = new HoneycombMarkerFactory();

        Utils utils = Utils.create();
        Logger logger = utils.getLogger("com.example.Test").get();

        // https://docs.honeycomb.io/working-with-your-data/tracing/send-trace-data/#manual-tracing
        String traceId = UUID.randomUUID().toString();
        SpanInfo method1Span = createRootSpan(traceId, "rootMethod", Instant.now().minusSeconds(3));
        LogstashMarker span1Marker = markerFactory.create(method1Span);

        SpanInfo method2Span = method1Span.childBuilder()
                .setName("childMethod")
                .setDurationSupplier(() -> Duration.between(Instant.now().minusSeconds(2), Instant.now()))
                .build();
        LogstashMarker span2Marker = markerFactory.create(method2Span);

        logger.info(span1Marker, "called first");
        logger.info(span2Marker, "called second");

        // TODO add an assertion
    }

    @Test
    public void testOnShutdown() {
        // On shutdown, the appender should dump the queue and send immediately
        // TODO add an assertion
    }


    private SpanInfo createRootSpan(String traceId, String methodName, Instant creationTime) {
        String serviceName = "sample_service";
        String spanId = UUID.randomUUID().toString();
        Supplier<Duration> durationSupplier = () -> Duration.between(creationTime, Instant.now());

        return SpanInfo.builder().setName(methodName)
                .setSpanId(spanId)
                .setTraceId(traceId)
                .setServiceName(serviceName)
                .setDurationSupplier(durationSupplier)
                .build();
    }


}
