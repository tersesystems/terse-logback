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
package com.tersesystems.logback.bytebuddy;

import com.typesafe.config.Config;
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

    public void initialize(Config config, Instrumentation instrumentation, boolean debug) {
        try {
            List<String> classNames = getClassNames(config);
            List<String> methodNames = getMethodNames(config);
            LoggingAdviceConfig loggingAdviceConfig = LoggingAdviceConfig.create(classNames, methodNames);
            AgentBuilder agentBuilder = new LoggingInstrumentationByteBuddyBuilder()
                    .builderFromConfigWithRetransformation(loggingAdviceConfig);

            // The debugging listener shows what classes are being picked up by the instrumentation
            if (debug) {
                AgentBuilder.Listener debugListener = createDebugListener(classNames);
                agentBuilder = agentBuilder.with(debugListener);
            }
            agentBuilder.installOn(instrumentation);
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
