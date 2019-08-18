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

import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.classic.Utils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.jupiter.api.Test;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static com.tersesystems.logback.honeycomb.HoneycombClient.DEFAULT_ACTORSYSTEM_NAME;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testClient() throws ExecutionException, InterruptedException, IOException {
        Utils utils = Utils.create();
        LoggingEventFactory loggingEventFactory = utils.getLoggingEventFactory();
        Logger logger = utils.getLogger("com.example.Test").get();

        HoneycombMarkerFactory markerService = new HoneycombMarkerFactory();
        ILoggingEvent loggingEvent = loggingEventFactory.create(null, logger, Level.INFO, "testClient", null, null);

        HoneycombAppender appender = utils.<HoneycombAppender>getAppender("HONEYCOMB").get();
        Encoder<ILoggingEvent> encoder = appender.getEncoder();

        final ActorSystem actorSystem = ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME);
        String honeycombApiKey = System.getenv("HONEYCOMB_API_KEY");

        HoneycombClient honeycombClient = createClient(honeycombApiKey, actorSystem, encoder);
        try {
            String dataSet = "terse-logback";
            HoneycombRequest honeycombRequest = new HoneycombRequest(1, loggingEvent);
            CompletionStage<HoneycombResponse> completionStage = honeycombClient.postEvent(dataSet, honeycombRequest);
            HoneycombResponse honeycombResponse = completionStage.toCompletableFuture().get();
            assertThat(honeycombResponse.isSuccess());
        } finally {
            honeycombClient.close();
            closeActorSystem(actorSystem);
        }
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

    private HoneycombClient createClient(String apiKey, ActorSystem actorSystem, Encoder<ILoggingEvent> encoder) {
        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
        clientMap.put("play.ws.useragent", "Logback Honeycomb Client");
        Config config =  ConfigFactory.parseMap(clientMap).withFallback(ConfigFactory.load());
        AhcWSClientConfig ahcWsClientConfig = AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader());
        return new HoneycombClient(ahcWsClientConfig, actorSystem, apiKey, encoder::encode);
    }

    private void closeActorSystem(ActorSystem actorSystem) {
        if (actorSystem != null) {
            CoordinatedShutdown shutdown = CoordinatedShutdown.get(actorSystem);
            shutdown.run(CoordinatedShutdown.unknownReason()); // I am too lazy to make up a reason.
        }
    }

}
