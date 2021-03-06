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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.tersesystems.logback.classic.StartTime;
import com.tersesystems.logback.honeycomb.client.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

/** Creates an appender that sends data to Honeycomb. */
public class HoneycombAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private String dataSet;
  private String apiKey;
  private Encoder<ILoggingEvent> encoder;
  private Integer sampleRate = 1;
  private Integer queueSize = 50;
  private BlockingQueue<HoneycombRequest<ILoggingEvent>> eventQueue;
  private boolean batch = true;
  private boolean includeCallerData = false;

  private HoneycombClient<ILoggingEvent> honeycombClient;

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

  public boolean isIncludeCallerData() {
    return includeCallerData;
  }

  public void setIncludeCallerData(boolean includeCallerData) {
    this.includeCallerData = includeCallerData;
  }

  protected void prepareForDeferredProcessing(ILoggingEvent event) {
    event.prepareForDeferredProcessing();
    if (includeCallerData) {
      event.getCallerData();
    }
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
      HoneycombClientService honeycombClientService = clientService();
      honeycombClient = honeycombClientService.newClient(apiKey, dataSet, this::serialize);
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
      } finally {
        honeycombClient = null;
      }
    }

    super.stop();
  }

  protected void dumpQueue() {
    try {
      // Post and then block until we get a response
      // Probably overkill, but we're shutting down in any case.
      if (!eventQueue.isEmpty()) {
        List<HoneycombRequest<ILoggingEvent>> list = new ArrayList<>();
        eventQueue.drainTo(list);
        postBatch(list).toCompletableFuture().get();
      }
    } catch (InterruptedException | ExecutionException e) {
      addError("drainQueue: Cannot generate JSON", e);
    }
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    try {
      prepareForDeferredProcessing(eventObject);
    } catch (RuntimeException e) {
      addWarn(
          "Unable to prepare event for deferred processing.  Event output might be missing data.",
          e);
    }

    Instant startTime = StartTime.from(context, eventObject);
    HoneycombRequest<ILoggingEvent> request =
        new HoneycombRequest<>(sampleRate, startTime, eventObject);

    if (batch) {
      // If queue is full, then drain and post it.
      if (!eventQueue.offer(request)) {
        List<HoneycombRequest<ILoggingEvent>> list = new ArrayList<>();

        // empty the queue
        eventQueue.drainTo(list);

        // put one back...
        eventQueue.offer(request);

        // post the contents.
        postBatch(list);
      }
    } else {
      postEvent(request);
    }
  }

  private CompletionStage<Void> postEvent(HoneycombRequest<ILoggingEvent> honeycombRequest) {
    return honeycombClient.post(honeycombRequest).thenAccept(this::accept);
  }

  private CompletionStage<Void> postBatch(
      Iterable<HoneycombRequest<ILoggingEvent>> honeycombRequests) {
    return honeycombClient.postBatch(honeycombRequests).thenAccept(this::accept);
  }

  private byte[] serialize(HoneycombRequest<ILoggingEvent> honeycombRequest) {
    return encoder.encode(honeycombRequest.getEvent());
  }

  private HoneycombClientService clientService() {
    ServiceLoader<HoneycombClientService> loader = ServiceLoader.load(HoneycombClientService.class);
    Optional<HoneycombClientService> first =
        StreamSupport.stream(loader.spliterator(), false).findFirst();
    if (first.isPresent()) {
      return first.get();
    }
    throw new IllegalStateException("No service found -- do you have a library loaded?");
  }

  private void accept(HoneycombResponse response) {
    if (!response.isSuccess()) {
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
  }

  private void accept(List<HoneycombResponse> responses) {
    for (HoneycombResponse response : responses) {
      if (!response.isSuccess()) {
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
  }
}
