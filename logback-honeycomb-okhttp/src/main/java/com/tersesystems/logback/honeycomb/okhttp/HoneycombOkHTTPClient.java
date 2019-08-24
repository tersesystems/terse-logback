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
import com.google.auto.service.AutoService;
import com.tersesystems.logback.honeycomb.client.*;
import okhttp3.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * This class implements a honeycomb client using OK HTTP.
 */
@AutoService(HoneycombClient.class)
public class HoneycombOkHTTPClient implements HoneycombClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final JsonFactory factory = new JsonFactory();
    private final OkHttpClient client;

    class OkHttpResponseFuture implements Callback {
        private final CompletableFuture<HoneycombResponse> future = new CompletableFuture<>();

        OkHttpResponseFuture() {}

        @Override public void onFailure(Call call, IOException e) {
            future.completeExceptionally(e);
        }

        @Override public void onResponse(Call call, Response response) throws IOException {
            HoneycombResponse honeycombResponse = new HoneycombResponse(response.code(), response.body().string());
            future.complete(honeycombResponse);
        }
    }

    class OkHttpBatchedResponseFuture implements Callback {
        private final CompletableFuture<List<HoneycombResponse>> future = new CompletableFuture<>();

        OkHttpBatchedResponseFuture() {}

        @Override public void onFailure(Call call, IOException e) {
            future.completeExceptionally(e);
        }

        @Override public void onResponse(Call call, Response response) throws IOException {
            List<HoneycombResponse> honeycombResponses = parseResponse(response);
            future.complete(honeycombResponses);
        }

        private List<HoneycombResponse> parseResponse(Response wsResponse) throws IOException {
            String body = wsResponse.body().string();
            List<HoneycombResponse> list = new ArrayList<>();

            JsonParser parser = factory.createParser(body);
            while(!parser.isClosed()){
                JsonToken jsonToken = parser.nextToken();

                if(JsonToken.FIELD_NAME.equals(jsonToken)){
                    String fieldName = parser.getCurrentName();
                    jsonToken = parser.nextToken();

                    String reason = "";
                    int status = 0;
                    if("error".equals(fieldName)){
                        reason = parser.getValueAsString();
                    } else if ("status".equals(fieldName)){
                        status = parser.getValueAsInt();
                    }
                    HoneycombResponse response = new HoneycombResponse(status, reason);
                    list.add(response);
                }
            }

            return list;
        }
    }

    public HoneycombOkHTTPClient() {
        //clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
        //clientMap.put("play.ws.useragent", "Logback Honeycomb Client");

        client = new OkHttpClient();
    }

    /**
     * Posts a single event to honeycomb, using the "1/events" endpoint.
     */
    @Override
    public <E> CompletionStage<HoneycombResponse> postEvent(String apiKey, String dataset, HoneycombRequest<E> honeycombRequest, Function<HoneycombRequest<E>, byte[]> encodeFunction) {
        String honeycombURL = eventURL(dataset);
        byte[] bytes = encodeFunction.apply(honeycombRequest);

        RequestBody body = RequestBody.create(JSON, bytes);
        Request request = new Request.Builder()
                .url(honeycombURL)
                .addHeader(HoneycombHeaders.teamHeader(), apiKey)
                .addHeader(HoneycombHeaders.eventTimeHeader(), isoTime(honeycombRequest.getTimestamp()))
                .addHeader(HoneycombHeaders.sampleRateHeader(), honeycombRequest.getSampleRate().toString())
                .post(body)
                .build();

        Call call = client.newCall(request);
        OkHttpResponseFuture result = new OkHttpResponseFuture();
        call.enqueue(result);
        return result.future;
    }

    @Override
    public <E> CompletionStage<List<HoneycombResponse>> postBatch(String apiKey, String dataset, List<HoneycombRequest<E>> requests, Function<HoneycombRequest<E>, byte[]> encodeFunction) {
        String honeycombURL = batchURL(dataset);
        try {
            byte[] batchedJson = generateBatchJson(requests, encodeFunction);
            RequestBody body = RequestBody.create(JSON, batchedJson);
            Request request = new Request.Builder().url(honeycombURL)
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

    public void close() throws IOException {
        client.dispatcher().executorService().shutdown();
    }

    private<E> byte[] generateBatchJson(List<HoneycombRequest<E>> requests, Function<HoneycombRequest<E>, byte[]> encodeFunction) throws IOException {
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

    class HoneycombRequestFormatter<E>  {
        private final JsonGenerator generator;
        private final Function<HoneycombRequest<E>, byte[]> encodeFunction;

        HoneycombRequestFormatter(JsonGenerator generator, Function<HoneycombRequest<E>, byte[]> encodeFunction) {
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

