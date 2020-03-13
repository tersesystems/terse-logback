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

import java.util.function.Function;

public interface HoneycombClientService {
  <E> HoneycombClient<E> newClient(
      String apiKey, String dataset, Function<HoneycombRequest<E>, byte[]> encodeFunction);
}
