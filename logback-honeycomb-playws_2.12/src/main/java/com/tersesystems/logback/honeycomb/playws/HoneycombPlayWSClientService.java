package com.tersesystems.logback.honeycomb.playws;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import com.tersesystems.logback.honeycomb.client.HoneycombClient;
import com.tersesystems.logback.honeycomb.client.HoneycombClientService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigFactory;
import play.libs.ws.ahc.StandaloneAhcWSClient;

import java.util.HashMap;
import java.util.Map;

public class HoneycombPlayWSClientService implements HoneycombClientService {

    public static final String DEFAULT_ACTORSYSTEM_NAME = "honeycombClientSystem";
    private static final String AKKA_MAX_THREADS_KEY =
            "akka.actor.default-dispatcher.fork-join-executor.parallelism-max";

    public HoneycombClient newClient() {
        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
        clientMap.put("play.ws.useragent", "Logback Honeycomb Client");
        clientMap.put(AKKA_MAX_THREADS_KEY, 2);
        Config config = ConfigFactory.parseMap(clientMap).withFallback(ConfigFactory.load());

        ActorSystem actorSystem = ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME, config);
        AhcWSClientConfig ahcWsClientConfig =
                AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader());
        StandaloneAhcWSClient standaloneAhcWSClient = StandaloneAhcWSClient.create(ahcWsClientConfig, createMaterializer(actorSystem));
        return new HoneycombPlayWSClient(standaloneAhcWSClient, actorSystem, true);
    }

    private static ActorMaterializer createMaterializer(ActorSystem system) {
        return ActorMaterializer.create(
                ActorMaterializerSettings.create(system), system, system.name());
    }

}
