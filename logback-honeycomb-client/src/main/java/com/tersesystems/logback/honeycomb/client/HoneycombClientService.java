package com.tersesystems.logback.honeycomb.client;

public interface HoneycombClientService {
    HoneycombClient newClient(String apiKey, String dataset);
}
