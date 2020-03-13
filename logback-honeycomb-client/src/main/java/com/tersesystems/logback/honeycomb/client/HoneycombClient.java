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
package com.tersesystems.logback.honeycomb.client;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public interface HoneycombClient<E> {

  CompletionStage<HoneycombResponse> post(HoneycombRequest<E> request);

  <F> CompletionStage<HoneycombResponse> post(
      HoneycombRequest<F> request, Function<HoneycombRequest<F>, byte[]> encodeFunction);

  CompletionStage<List<HoneycombResponse>> postBatch(Iterable<HoneycombRequest<E>> requests);

  <F> CompletionStage<List<HoneycombResponse>> postBatch(
      Iterable<HoneycombRequest<F>> requests, Function<HoneycombRequest<F>, byte[]> encodeFunction);

  CompletionStage<Void> close();
}
