package com.tersesystems.logback.bytebuddy;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.lang.instrument.Instrumentation;

public class LogbackInstrumentationAgent extends LogbackInstrumentation {

    public static void premain(String arg, Instrumentation instrumentation) throws Exception {
        Config config = ConfigFactory.load();
        LogbackInstrumentationAgent agent = new LogbackInstrumentationAgent();
        agent.initialize(config, instrumentation);
    }

    public static void agentmain(String arg, Instrumentation inst) {
        Config config = ConfigFactory.load();
        LogbackInstrumentationAgent agent = new LogbackInstrumentationAgent();
        agent.initialize(config, inst);
    }

}
