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

public interface HoneycombClient<E> {

  CompletionStage<HoneycombResponse> postEvent(HoneycombRequest<E> request);

  CompletionStage<List<HoneycombResponse>> postBatch(Iterable<HoneycombRequest<E>> requests);

  void close() throws IOException;
}
