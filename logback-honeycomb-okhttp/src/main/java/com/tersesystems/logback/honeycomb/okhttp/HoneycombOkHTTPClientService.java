package com.tersesystems.logback.honeycomb.okhttp;

import com.fasterxml.jackson.core.JsonFactory;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombClientService;
import okhttp3.OkHttpClient;

public class HoneycombOkHTTPClientService implements HoneycombClientService {
  @Override
  public HoneycombClient newClient(String apiKey, String dataset) {
    return new HoneycombOkHTTPClient(new OkHttpClient(), new JsonFactory(), apiKey, dataset);
  }
}
