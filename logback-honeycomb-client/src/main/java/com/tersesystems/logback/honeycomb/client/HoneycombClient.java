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
package com.tersesystems.logback.honeycomb.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface HoneycombClient {

  <E> CompletionStage<HoneycombResponse> postEvent(
      String apiKey,
      String dataset,
      HoneycombRequest<E> request,
      Function<HoneycombRequest<E>, byte[]> encodeFunction);

  <E> CompletionStage<List<HoneycombResponse>> postBatch(
      String apiKey,
      String dataset,
      List<HoneycombRequest<E>> requests,
      Function<HoneycombRequest<E>, byte[]> encodeFunction);

  void close() throws IOException;
}
