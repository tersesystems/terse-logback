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
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.auto.service.AutoService;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombHeaders;
import com.tersesystems.logback.honeycomb.client.HoneycombRequest;
import com.tersesystems.logback.honeycomb.client.HoneycombResponse;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigFactory;
import play.libs.ws.DefaultBodyReadables;
import play.libs.ws.DefaultBodyWritables;
import play.libs.ws.StandaloneWSResponse;
import play.libs.ws.ahc.StandaloneAhcWSClient;
import play.libs.ws.ahc.StandaloneAhcWSRequest;

@AutoService(HoneycombClient.class)
public class HoneycombPlayWSClient
    implements HoneycombClient, DefaultBodyWritables, DefaultBodyReadables {

  public static final String DEFAULT_ACTORSYSTEM_NAME = "honeycombClientSystem";
  private static final String AKKA_MAX_THREADS_KEY =
      "akka.actor.default-dispatcher.fork-join-executor.parallelism-max";

  private final StandaloneAhcWSClient client;
  private final JsonFactory factory = new JsonFactory();
  private final ActorSystem actorSystem;

  public HoneycombPlayWSClient() {
    Map<String, Object> clientMap = new HashMap<>();
    clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
    clientMap.put("play.ws.useragent", "Logback Honeycomb Client");
    clientMap.put(AKKA_MAX_THREADS_KEY, 2);
    Config config = ConfigFactory.parseMap(clientMap).withFallback(ConfigFactory.load());

    actorSystem = ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME, config);
    AhcWSClientConfig ahcWsClientConfig =
        AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader());
    this.client = StandaloneAhcWSClient.create(ahcWsClientConfig, createMaterializer(actorSystem));
  }

  /** Posts a single event to honeycomb, using the "1/events" endpoint. */
  @Override
  public <E> CompletionStage<HoneycombResponse> postEvent(
      String apiKey,
      String dataset,
      HoneycombRequest<E> request,
      Function<HoneycombRequest<E>, byte[]> encodeFunction) {
    String honeycombURL = eventURL(dataset);
    StandaloneAhcWSRequest ahcWSRequest = client.url(honeycombURL);
    byte[] bytes = encodeFunction.apply(request);
    return ahcWSRequest
        .addHeader(HoneycombHeaders.teamHeader(), apiKey)
        .addHeader(HoneycombHeaders.eventTimeHeader(), isoTime(request.getTimestamp()))
        .addHeader(HoneycombHeaders.sampleRateHeader(), request.getSampleRate().toString())
        .addHeader("Content-Type", "application/json")
        .post(body(bytes))
        .thenApply(response -> new HoneycombResponse(response.getStatus(), response.getBody()));
  }

  @Override
  public <E> CompletionStage<List<HoneycombResponse>> postBatch(
      String apiKey,
      String dataset,
      List<HoneycombRequest<E>> requests,
      Function<HoneycombRequest<E>, byte[]> encodeFunction) {
    String honeycombURL = batchURL(dataset);
    try {
      StandaloneAhcWSRequest ahcWSRequest = client.url(honeycombURL);
      byte[] batchedJson = generateBatchJson(requests, encodeFunction);
      return ahcWSRequest
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

  private ActorMaterializer createMaterializer(ActorSystem system) {
    return ActorMaterializer.create(
        ActorMaterializerSettings.create(system), system, system.name());
  }

  public void close() throws IOException {
    client.close();
    actorSystem.terminate();
  }

  private <E> byte[] generateBatchJson(
      List<HoneycombRequest<E>> requests, Function<HoneycombRequest<E>, byte[]> encodeFunction)
      throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    JsonGenerator generator = factory.createGenerator(stream);
    HoneycombRequestFormatter formatter = new HoneycombRequestFormatter(generator, encodeFunction);

    formatter.start();
    for (HoneycombRequest request : requests) {
      formatter.format(request);
    }
    formatter.end();
    generator.close();

    return stream.toByteArray();
  }

  class HoneycombRequestFormatter<E> {
    private final JsonGenerator generator;
    private final Function<HoneycombRequest<E>, byte[]> encodeFunction;

    HoneycombRequestFormatter(
        JsonGenerator generator, Function<HoneycombRequest<E>, byte[]> encodeFunction) {
      this.generator = generator;
      this.encodeFunction = encodeFunction;
    }

    void start() throws IOException {
      this.generator.writeStartArray();
    }

    void end() throws IOException {
      this.generator.writeEndArray();
    }

    void format(HoneycombRequest request) throws IOException {
      byte[] bytes = encodeFunction.apply(request);

      generator.writeStartObject();
      generator.writeStringField("time", isoTime(request.getTimestamp()));
      generator.writeNumberField("samplerate", request.getSampleRate());
      generator.writeFieldName("data");
      generator.writeRaw(":");
      generator.writeRaw(new String(bytes, StandardCharsets.UTF_8));
      generator.writeEndObject();
    }
  }

  private String isoTime(Instant eventTime) {
    return DateTimeFormatter.ISO_INSTANT.format(eventTime);
  }
}
