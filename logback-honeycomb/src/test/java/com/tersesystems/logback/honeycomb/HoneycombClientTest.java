package com.tersesystems.logback.honeycomb;

import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.joran.spi.JoranException;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.classic.Utils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import play.api.libs.ws.ahc.AhcWSClientConfig;
import play.api.libs.ws.ahc.AhcWSClientConfigFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static com.tersesystems.logback.honeycomb.HoneycombClient.DEFAULT_ACTORSYSTEM_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class HoneycombClientTest {

    @Test
    public void testClient() throws Exception {
        Utils utils = Utils.create("/logback-honeycomb-event.xml");

        LoggingEventFactory loggingEventFactory = utils.getLoggingEventFactory();
        Logger logger = utils.getLogger("com.example.Test").get();

        ILoggingEvent loggingEvent = loggingEventFactory.create(null, logger, Level.INFO, "testClient", null, null);

        HoneycombAppender appender = utils.<HoneycombAppender>getAppender("HONEYCOMB").get();
        Encoder<ILoggingEvent> encoder = appender.getEncoder();

        final ActorSystem actorSystem = ActorSystem.create(DEFAULT_ACTORSYSTEM_NAME);
        String honeycombApiKey = System.getenv("HONEYCOMB_API_KEY");

        HoneycombClient honeycombClient = createClient(honeycombApiKey, actorSystem, encoder);
        try {
            String dataSet = "terse-logback";
            HoneycombRequest honeycombRequest = new HoneycombRequest(1, loggingEvent);
            CompletionStage<HoneycombResponse> completionStage = honeycombClient.postEvent(dataSet, honeycombRequest);
            HoneycombResponse honeycombResponse = completionStage.toCompletableFuture().get();
            assertThat(honeycombResponse.isSuccess());
        } finally {
            honeycombClient.close();
            closeActorSystem(actorSystem);
        }
    }

    private HoneycombClient createClient(String apiKey, ActorSystem actorSystem, Encoder<ILoggingEvent> encoder) {
        Map<String, Object> clientMap = new HashMap<>();
        clientMap.put("play.ws.compressionEnabled", Boolean.TRUE);
        clientMap.put("play.ws.useragent", "Logback Honeycomb Client");
        Config config =  ConfigFactory.parseMap(clientMap).withFallback(ConfigFactory.load());
        AhcWSClientConfig ahcWsClientConfig = AhcWSClientConfigFactory.forConfig(config, config.getClass().getClassLoader());
        return new HoneycombClient(ahcWsClientConfig, actorSystem, apiKey, encoder::encode);
    }

    private void closeActorSystem(ActorSystem actorSystem) {
        if (actorSystem != null) {
            CoordinatedShutdown shutdown = CoordinatedShutdown.get(actorSystem);
            shutdown.run(CoordinatedShutdown.unknownReason()); // I am too lazy to make up a reason.
        }
    }
}
