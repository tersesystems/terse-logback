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

package com.tersesystems.logback.honeycomb.playws;

import com.fasterxml.jackson.core.JsonGenerator;
import com.tersesystems.logback.honeycomb.client.HoneycombRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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

  static String isoTime(Instant eventTime) {
    return DateTimeFormatter.ISO_INSTANT.format(eventTime);
  }
}
