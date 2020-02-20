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

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.classic.Utils;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombRequest;
import com.tersesystems.logback.honeycomb.client.HoneycombResponse;
import com.tersesystems.logback.honeycomb.playws.HoneycombPlayWSClientService;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class HoneycombClientTest {

  public void notATest() throws Exception {
    Utils utils = Utils.create("/logback-honeycomb-event.xml");

    LoggingEventFactory loggingEventFactory = utils.getLoggingEventFactory();
    Logger logger = utils.getLogger("com.example.Test");

    ILoggingEvent loggingEvent =
        loggingEventFactory.create(null, logger, Level.INFO, "testClient", null, null);

    HoneycombAppender appender = utils.<HoneycombAppender>getAppender("HONEYCOMB").get();
    Encoder<ILoggingEvent> encoder = appender.getEncoder();

    String honeycombApiKey = System.getenv("HONEYCOMB_API_KEY");
    String dataSet = "terse-logback";
    HoneycombClient<ILoggingEvent> honeycombClient =
        createClient(honeycombApiKey, dataSet, e -> encoder.encode(e.getEvent()));
    try {
      HoneycombRequest<ILoggingEvent> honeycombRequest =
          new HoneycombRequest<>(1, Instant.now(), loggingEvent);
      CompletionStage<HoneycombResponse> completionStage =
          honeycombClient.postEvent(honeycombRequest);
      HoneycombResponse honeycombResponse = completionStage.toCompletableFuture().get();
      assertThat(honeycombResponse.isSuccess());
    } finally {
      honeycombClient.close();
    }
  }

  private HoneycombClient<ILoggingEvent> createClient(
      String honeycombApiKey,
      String dataSet,
      Function<HoneycombRequest<ILoggingEvent>, byte[]> encodeFunction) {
    HoneycombPlayWSClientService service = new HoneycombPlayWSClientService();
    return service.newClient(honeycombApiKey, dataSet, encodeFunction);
  }
}
