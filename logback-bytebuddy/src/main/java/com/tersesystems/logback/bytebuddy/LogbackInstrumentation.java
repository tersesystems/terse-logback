package com.tersesystems.logback.bytebuddy;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * Agent that instruments classes before the main JVM starts.
 *
 * You must run this if you want to instrument classes loaded from the system classloader,
 * i.e. packages in rt/classes.jar
 */
public abstract class LogbackInstrumentation {

    public void initialize(Config config, Instrumentation instrumentation) {
        try {
            List<String> classNames = getClassNames(config);
            List<String> methodNames = getMethodNames(config);
            ClassAdviceConfig classAdviceConfig = ClassAdviceConfig.create(classNames, methodNames);

            // The debugging listener shows what classes are being picked up by the instrumentation
            AgentBuilder.Listener debugListener = createDebugListener(classNames);
            new LoggingInstrumentationByteBuddyBuilder()
                    .builderFromConfigWithRetransformation(classAdviceConfig)
                    .with(debugListener)
                    .installOn(instrumentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return ClassAdviceUtils.createDebugListener(classNames);
    }

    protected List<String> getMethodNames(Config config) {
        return config.getStringList("logback.bytebuddy.methodNames");
    }

    protected List<String> getClassNames(Config config) {
        return config.getStringList("logback.bytebuddy.classNames");
    }

}
