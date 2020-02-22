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
package com.tersesystems.logback.honeycomb.okhttp;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.tersesystems.logback.honeycomb.client.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import okhttp3.*;

/** This class implements a honeycomb client using OK HTTP. */
public class HoneycombOkHTTPClient<E> implements HoneycombClient<E> {
  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final JsonFactory jsonFactory;
  private final OkHttpClient client;
  private final String apiKey;
  private final String dataset;
  private final Function<HoneycombRequest<E>, byte[]> defaultEncodeFunction;

  public HoneycombOkHTTPClient(
      OkHttpClient client,
      JsonFactory jsonFactory,
      String apiKey,
      String dataset,
      Function<HoneycombRequest<E>, byte[]> defaultEncodeFunction) {
    this.client = client;
    this.jsonFactory = jsonFactory;
    this.dataset = dataset;
    this.apiKey = apiKey;
    this.defaultEncodeFunction = defaultEncodeFunction;
  }

  /** Posts a single event to honeycomb, using the "1/events" endpoint. */
  @Override
  public CompletionStage<HoneycombResponse> post(HoneycombRequest<E> honeycombRequest) {
    return post(honeycombRequest, this.defaultEncodeFunction);
  }

  @Override
  public <F> CompletionStage<HoneycombResponse> post(
      HoneycombRequest<F> honeycombRequest, Function<HoneycombRequest<F>, byte[]> encodeFunction) {
    String honeycombURL = eventURL(dataset);
    byte[] bytes = encodeFunction.apply(honeycombRequest);

    RequestBody body = RequestBody.create(bytes, JSON);
    Request request =
        new Request.Builder()
            .url(honeycombURL)
            .addHeader(HoneycombHeaders.teamHeader(), apiKey)
            .addHeader(HoneycombHeaders.eventTimeHeader(), isoTime(honeycombRequest.getTimestamp()))
            .addHeader(
                HoneycombHeaders.sampleRateHeader(), honeycombRequest.getSampleRate().toString())
            .post(body)
            .build();

    Call call = client.newCall(request);
    OkHttpResponseFuture result = new OkHttpResponseFuture();
    call.enqueue(result);
    return result.future;
  }

  @Override
  public CompletionStage<List<HoneycombResponse>> postBatch(
      Iterable<HoneycombRequest<E>> requests) {
    return postBatch(requests, this.defaultEncodeFunction);
  }

  @Override
  public <F> CompletionStage<List<HoneycombResponse>> postBatch(
      Iterable<HoneycombRequest<F>> honeycombRequests,
      Function<HoneycombRequest<F>, byte[]> encodeFunction) {
    String honeycombURL = batchURL(dataset);
    try {
      byte[] batchedJson = generateBatchJson(honeycombRequests, encodeFunction);
      RequestBody body = RequestBody.create(batchedJson, JSON);
      Request request =
          new Request.Builder()
              .url(honeycombURL)
              .post(body)
              .addHeader(HoneycombHeaders.teamHeader(), apiKey)
              .build();

      Call call = client.newCall(request);
      OkHttpBatchedResponseFuture result = new OkHttpBatchedResponseFuture();
      call.enqueue(result);
      return result.future;
    } catch (IOException e) {
      throw new IllegalStateException("should never happen", e);
    }
  }

  private String eventURL(String dataset) {
    String eventURL = "https://api.honeycomb.io/1/events/";
    return eventURL + dataset;
  }

  private String batchURL(String dataset) {
    String batchURL = "https://api.honeycomb.io/1/batch/";
    return batchURL + dataset;
  }

  public CompletionStage<Void> close() {
    return CompletableFuture.runAsync(
        () -> {
          client.dispatcher().executorService().shutdown();
        });
  }

  private <F> byte[] generateBatchJson(
      Iterable<HoneycombRequest<F>> requests, Function<HoneycombRequest<F>, byte[]> encodeFunction)
      throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    JsonGenerator generator = jsonFactory.createGenerator(stream);
    HoneycombRequestFormatter formatter = new HoneycombRequestFormatter(generator);

    formatter.start();
    for (HoneycombRequest<F> request : requests) {
      formatter.format(request, encodeFunction);
    }
    formatter.end();
    generator.close();

    return stream.toByteArray();
  }

  private String isoTime(Instant eventTime) {
    return DateTimeFormatter.ISO_INSTANT.format(eventTime);
  }

  class HoneycombRequestFormatter<F> {
    private final JsonGenerator generator;

    HoneycombRequestFormatter(JsonGenerator generator) {
      this.generator = generator;
    }

    void start() throws IOException {
      this.generator.writeStartArray();
    }

    void end() throws IOException {
      this.generator.writeEndArray();
    }

    void format(HoneycombRequest<F> request, Function<HoneycombRequest<F>, byte[]> encodeFunction)
        throws IOException {
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

  static class OkHttpResponseFuture implements Callback {
    private final CompletableFuture<HoneycombResponse> future = new CompletableFuture<>();

    OkHttpResponseFuture() {}

    @Override
    public void onFailure(Call call, IOException e) {
      future.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
      HoneycombResponse honeycombResponse =
          new HoneycombResponse(response.code(), response.body().string());
      future.complete(honeycombResponse);
    }
  }

  class OkHttpBatchedResponseFuture implements Callback {
    private final CompletableFuture<List<HoneycombResponse>> future = new CompletableFuture<>();

    OkHttpBatchedResponseFuture() {}

    @Override
    public void onFailure(Call call, IOException e) {
      future.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
      List<HoneycombResponse> honeycombResponses = parseResponse(response);
      future.complete(honeycombResponses);
    }

    private List<HoneycombResponse> parseResponse(Response wsResponse) throws IOException {
      String body = wsResponse.body().string();
      List<HoneycombResponse> list = new ArrayList<>();

      JsonParser parser = jsonFactory.createParser(body);
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

      return list;
    }
  }
}
