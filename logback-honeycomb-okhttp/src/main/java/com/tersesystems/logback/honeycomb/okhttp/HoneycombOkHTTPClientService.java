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

package com.tersesystems.logback.honeycomb.okhttp;

import com.fasterxml.jackson.core.JsonFactory;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombClientService;
import com.tersesystems.logback.honeycomb.client.HoneycombRequest;
import java.util.function.Function;
import okhttp3.OkHttpClient;

public class HoneycombOkHTTPClientService implements HoneycombClientService {
  @Override
  public <E> HoneycombClient<E> newClient(
      String apiKey, String dataset, Function<HoneycombRequest<E>, byte[]> encodeFunction) {
    return new HoneycombOkHTTPClient(
        new OkHttpClient(), new JsonFactory(), apiKey, dataset, encodeFunction);
  }
}
