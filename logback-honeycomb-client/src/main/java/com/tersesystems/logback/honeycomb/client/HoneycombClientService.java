package com.tersesystems.logback.honeycomb.client;

import java.util.function.Function;

public interface HoneycombClientService {
  <E> HoneycombClient<E> newClient(
      String apiKey, String dataset, Function<HoneycombRequest<E>, byte[]> encodeFunction);
}
