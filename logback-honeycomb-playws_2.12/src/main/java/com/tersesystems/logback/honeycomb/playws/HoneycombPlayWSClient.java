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
package com.tersesystems.logback.honeycomb.playws;

import akka.actor.ActorSystem;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombHeaders;
import com.tersesystems.logback.honeycomb.client.HoneycombRequest;
import com.tersesystems.logback.honeycomb.client.HoneycombResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import play.libs.ws.*;

public class HoneycombPlayWSClient<E>
    implements HoneycombClient<E>, DefaultBodyWritables, DefaultBodyReadables {

  private final StandaloneWSClient client;
  private final JsonFactory factory = new JsonFactory();
  private final ActorSystem actorSystem;
  private final String apiKey;
  private final String dataset;
  private final Function<HoneycombRequest<E>, byte[]> encodeFunction;
  private final boolean terminateOnClose;

  public HoneycombPlayWSClient(
      StandaloneWSClient client,
      ActorSystem actorSystem,
      String apiKey,
      String dataset,
      Function<HoneycombRequest<E>, byte[]> encodeFunction,
      boolean terminateOnClose) {
    this.client = client;
    this.actorSystem = actorSystem;
    this.apiKey = apiKey;
    this.dataset = dataset;
    this.encodeFunction = encodeFunction;
    this.terminateOnClose = terminateOnClose;
  }

  /** Posts a single event to honeycomb, using the "1/events" endpoint. */
  @Override
  public CompletionStage<HoneycombResponse> postEvent(HoneycombRequest<E> request) {
    String honeycombURL = eventURL(dataset);
    StandaloneWSRequest wsRequest = client.url(honeycombURL);
    byte[] bytes = encodeFunction.apply(request);
    return wsRequest
        .addHeader(HoneycombHeaders.teamHeader(), apiKey)
        .addHeader(
            HoneycombHeaders.eventTimeHeader(),
            HoneycombRequestFormatter.isoTime(request.getTimestamp()))
        .addHeader(HoneycombHeaders.sampleRateHeader(), request.getSampleRate().toString())
        .addHeader("Content-Type", "application/json")
        .post(body(bytes))
        .thenApply(response -> new HoneycombResponse(response.getStatus(), response.getBody()));
  }

  @Override
  public CompletionStage<List<HoneycombResponse>> postBatch(
      Iterable<HoneycombRequest<E>> requests) {
    String honeycombURL = batchURL(dataset);
    try {
      StandaloneWSRequest wsRequest = client.url(honeycombURL);
      byte[] batchedJson = generateBatchJson(requests, encodeFunction);
      return wsRequest
          .addHeader(HoneycombHeaders.teamHeader(), apiKey)
          .addHeader("Content-Type", "application/json")
          .post(body(batchedJson))
          .thenApply(this::parseResponse);
    } catch (IOException e) {
      throw new IllegalStateException("should never happen", e);
    }
  }

  private List<HoneycombResponse> parseResponse(StandaloneWSResponse wsResponse) {
    String body = wsResponse.getBody();
    List<HoneycombResponse> list = new ArrayList<>();
    try {
      JsonParser parser = factory.createParser(body);
      while (!parser.isClosed()) {
        JsonToken jsonToken = parser.nextToken();

        if (JsonToken.FIELD_NAME.equals(jsonToken)) {
          String fieldName = parser.getCurrentName();
          jsonToken = parser.nextToken();

          String reason = "";
          int status = 0;
          if ("error".equals(fieldName)) {
            reason = parser.getValueAsString();
          } else if ("status".equals(fieldName)) {
            status = parser.getValueAsInt();
          }
          HoneycombResponse response = new HoneycombResponse(status, reason);
          list.add(response);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("should never happen", e);
    }

    return list;
  }

  private String eventURL(String dataset) {
    String eventURL = "https://api.honeycomb.io/1/events/";
    return eventURL + dataset;
  }

  private String batchURL(String dataset) {
    String batchURL = "https://api.honeycomb.io/1/batch/";
    return batchURL + dataset;
  }

  public void close() throws IOException {
    client.close();
    if (terminateOnClose) {
      actorSystem.terminate();
    }
  }

  private byte[] generateBatchJson(
      Iterable<HoneycombRequest<E>> requests, Function<HoneycombRequest<E>, byte[]> encodeFunction)
      throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    JsonGenerator generator = factory.createGenerator(stream);
    HoneycombRequestFormatter<E> formatter =
        new HoneycombRequestFormatter<>(generator, encodeFunction);

    formatter.start();
    for (HoneycombRequest<E> request : requests) {
      formatter.format(request);
    }
    formatter.end();
    generator.close();

    return stream.toByteArray();
  }
}
