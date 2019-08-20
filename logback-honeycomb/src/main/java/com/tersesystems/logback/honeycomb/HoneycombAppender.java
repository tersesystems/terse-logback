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
import akka.actor.BootstrapSetup;
import akka.actor.setup.ActorSystemSetup;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static com.tersesystems.logback.honeycomb.HoneycombClient.DEFAULT_ACTORSYSTEM_NAME;

/**
 * Creates an appender that sends data to Honeycomb.
 */
public class HoneycombAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final String AKKA_MAX_THREADS_KEY = "akka.actor.default-dispatcher.fork-join-executor.parallelism-max";

    private String dataSet;
    private String apiKey;
    private Encoder<ILoggingEvent> encoder;
    private Integer sampleRate = 1;
    private Integer queueSize = 50;
    private BlockingQueue<HoneycombRequest> eventQueue;
    private boolean batch = true;

    private ActorSystem actorSystem;
    private HoneycombClient honeycombClient;

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    @Override
    public void start() {
        boolean errorsFound = false;
        if (encoder == null) {
            addError("No encoder found!");
            errorsFound = true;
        }

        if (apiKey == null) {
            addError("No apiKey found!");
            errorsFound = true;
        }

        if (dataSet == null) {
            addError("No dataSet found!");
            errorsFound = true;
        }

        if (errorsFound) {
            return;
        }

        try {
            // We don't need a big actor system here.
            Map<String, ?> cfg = Collections.singletonMap(AKKA_MAX_THREADS_KEY, 2);
            Config config = ConfigFactory.parseMap(cfg).withFallback(ConfigFactory.load());
            actorSystem = ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME, config);
            honeycombClient = createClient(apiKey, actorSystem);
            if (batch) {
                eventQueue = new ArrayBlockingQueue<>(queueSize);
            }
            super.start();
        } catch (Exception e) {
            addError("Cannot start appender!", e);
        }
    }

    @Override
    public void stop() {
        if (started && batch) {
            dumpQueue();
        }

        if (honeycombClient != null) {
            try {
                honeycombClient.close();
            } catch (IOException e) {
                addError("Cannot close client cleanly", e);
            } finally {
                honeycombClient = null;
            }
        }
        closeActorSystem();

        super.stop();
    }

    protected void dumpQueue() {
        try {
            // Post and then block until we get a response
            // Probably overkill, but we're shutting down in any case.
            if (! eventQueue.isEmpty()) {
                List<HoneycombRequest> list = new ArrayList<>();
                eventQueue.drainTo(list);
                postBatch(list).toCompletableFuture().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            addError("drainQueue: Cannot generate JSON", e);
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (batch) {
            // If queue is full, then drain and post it.
            HoneycombRequest request = new HoneycombRequest(sampleRate, eventObject);
            if (! eventQueue.offer(request)) {
                List<HoneycombRequest> list = new ArrayList<>();
                eventQueue.drainTo(list);
                eventQueue.offer(request);
                postBatch(list);
            }
        } else {
            HoneycombRequest honeycombRequest = new HoneycombRequest(sampleRate, eventObject);
            postEvent(honeycombRequest);
        }
    }

    private CompletionStage<Void> postEvent(HoneycombRequest honeycombRequest) {
        return honeycombClient.postEvent(dataSet, honeycombRequest).thenAccept(response -> {
            if (! response.isSuccess()) {
                if (response.isRateLimited()) {
                    addInfo("postEvent: Rate Limited: " + response.getReason());
                } else if (response.isBlacklisted() || response.isInvalidKey()) {
                    addError("postEvent: Unrecoverable error: " + response.getReason());
                } else {
                    addWarn("postEvent: Transient error: " + response.getReason());
                }
            } else {
                addInfo("postEvent: successful post");
            }
        });
    }

    private CompletionStage<Void> postBatch(List<HoneycombRequest> list) {
        return honeycombClient.postBatch(dataSet, list).thenAccept(responses -> {
            for (HoneycombResponse response : responses) {
                if (! response.isSuccess()) {
                    if (response.isRateLimited()) {
                        addInfo("postBatch: Rate Limited: " + response.getReason());
                    } else if (response.isBlacklisted() || response.isInvalidKey()) {
                        addError("postBatch: Unrecoverable error: " + response.getReason());
                    } else {
                        addWarn("postBatch: Transient error: " + response.getReason());
                    }
                } else {
                    addInfo("postBatch: successful post");
                }
            }
        });
    }

    private HoneycombClient createClient(String apiKey, ActorSystem actorSystem) {
        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
        clientMap.put("play.ws.useragent", "Logback Honeycomb Client");
        Config config =  ConfigFactory.parseMap(clientMap).withFallback(ConfigFactory.load());
        AhcWSClientConfig ahcWsClientConfig = AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader());
        return new HoneycombClient(ahcWsClientConfig, actorSystem, apiKey, encoder::encode);
    }

    private void closeActorSystem() {
        if (actorSystem != null) {
            actorSystem.terminate();
            actorSystem = null;
        }
    }
}
