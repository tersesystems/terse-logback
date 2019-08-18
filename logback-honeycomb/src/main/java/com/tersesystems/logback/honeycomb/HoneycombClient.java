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
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.logstash.logback.composite.JsonWritingUtils;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.libs.ws.DefaultBodyReadables;
import play.libs.ws.DefaultBodyWritables;
import play.libs.ws.StandaloneWSResponse;
import play.libs.ws.ahc.AhcWSClientConfigFactory;
import play.libs.ws.ahc.StandaloneAhcWSClient;
import play.libs.ws.ahc.StandaloneAhcWSRequest;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.util.Objects.requireNonNull;

public class HoneycombClient implements DefaultBodyWritables, DefaultBodyReadables, Closeable {

    public static final String LOGBACK_HONEYCOMB_KEY = "logback.honeycomb.key";
    public static final String DEFAULT_ACTORSYSTEM_NAME = "honeycombClientSystem";

    private final StandaloneAhcWSClient client;
    private final Function<ILoggingEvent, byte[]> encodeFunction;
    private final String apiKey;
    private final JsonFactory factory = new JsonFactory();

    /**
     * Creates a honeycomb client, pulling config from the default `ConfigFactory.load`.
     */
    public HoneycombClient(Function<ILoggingEvent, byte[]> encodeFunction) {
        this(ConfigFactory.load(), encodeFunction);
    }

    /**
     * Creates a honeycomb client, pulling the api key from "logback.honeycomb.key" in the config.
     */
    public HoneycombClient(Config config, Function<ILoggingEvent, byte[]> encodeFunction) {
        this(AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader()),
                ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME, config),
                config.getString(LOGBACK_HONEYCOMB_KEY), encodeFunction);
    }

    public HoneycombClient(AhcWSClientConfig ahcWSClientConfig,
                           ActorSystem system,
                           String apiKey,
                           Function<ILoggingEvent, byte[]> encodeFunction) {
        this.apiKey = requireNonNull(apiKey, "Null apiKey");
        this.client = StandaloneAhcWSClient.create(
                ahcWSClientConfig,
                createMaterializer(system)
        );
        this.encodeFunction = encodeFunction;
    }

    /**
     * Posts a single event to honeycomb, using the "1/events" endpoint.
     */
    public CompletionStage<HoneycombResponse> postEvent(String dataset, HoneycombRequest request) {
        String honeycombURL = eventURL(dataset);
        StandaloneAhcWSRequest ahcWSRequest = client.url(honeycombURL);
        byte[] bytes = encodeFunction.apply(request.getEvent());
        return ahcWSRequest
                .addHeader(HoneycombHeaders.teamHeader(), apiKey)
                .addHeader(HoneycombHeaders.eventTimeHeader(), request.getTimestamp())
                .addHeader(HoneycombHeaders.sampleRateHeader(), request.getSampleRate().toString())
                .addHeader("Content-Type", "application/json")
                .post(body(bytes)).thenApply(response ->
                        new HoneycombResponse(response.getStatus(), response.getBody()));
    }

    public CompletionStage<List<HoneycombResponse>> postBatch(String dataset, List<HoneycombRequest> requests) {
        String honeycombURL = batchURL(dataset);
        try {
            StandaloneAhcWSRequest ahcWSRequest = client.url(honeycombURL);
            byte[] batchedJson = generateBatchJson(requests);
            //System.out.println(new String(batchedJson, StandardCharsets.UTF_8));
            return ahcWSRequest
                    .addHeader(HoneycombHeaders.teamHeader(), apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body(batchedJson)).thenApply(this::parseResponse);
        } catch (IOException e) {
            throw new IllegalStateException("should never happen", e);
        }
    }

    private List<HoneycombResponse> parseResponse(StandaloneWSResponse wsResponse) {
        String body = wsResponse.getBody();
        List<HoneycombResponse> list = new ArrayList<>();
        try {
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
                ActorMaterializerSettings.create(system),
                system,
                system.name()
        );
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    private byte[] generateBatchJson(List<HoneycombRequest> requests) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(stream);
        HoneycombRequestFormatter formatter = new HoneycombRequestFormatter(generator);

        formatter.start();
        for (HoneycombRequest request : requests) {
            formatter.format(request);
        }
        formatter.end();
        generator.close();

        return stream.toByteArray();
    }

    class HoneycombRequestFormatter {
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

        void format(HoneycombRequest request) throws IOException {
            byte[] bytes = encodeFunction.apply(request.getEvent());
            Instant eventTimestamp = Instant.ofEpochMilli(request.getEvent().getTimeStamp());

            generator.writeStartObject();
            JsonWritingUtils.writeStringField(generator, "time", ISO_INSTANT.format(eventTimestamp));
            JsonWritingUtils.writeNumberField(generator, "samplerate", request.getSampleRate());
            generator.writeFieldName("data");
            generator.writeRaw(":");
            generator.writeRaw(new String(bytes, StandardCharsets.UTF_8));
            generator.writeEndObject();
        }
    }
}

