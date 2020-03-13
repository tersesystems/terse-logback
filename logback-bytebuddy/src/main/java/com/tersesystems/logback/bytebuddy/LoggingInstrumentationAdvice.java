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
package com.tersesystems.logback.bytebuddy;

import com.tersesystems.logback.bytebuddy.impl.SystemFlow;
import com.typesafe.config.*;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.StringMatcher;

/** The code to be added on entry / exit to the methods under instrumentation. */
public class LoggingInstrumentationAdvice {

  private static final String LOGBACK = "logback";

  private static final String LOGBACK_TEST = "logback-test";

  private static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

  private static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

  private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

  // We need to load the implementation of enter / exit methods from the system classloader,
  // so that we don't end up hauling SLF4J impl factory into bootstrap classloader, which
  // will hopelessly confuse the JVM.
  public static Method enterMethod;

  static {
    try {
      String className = "com.tersesystems.logback.bytebuddy.impl.Enter";
      Class<?> enterClass = systemClassLoader.loadClass(className);
      enterMethod = enterClass.getMethod("apply", String.class, Object[].class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Method exitMethod;

  static {
    try {
      String className = "com.tersesystems.logback.bytebuddy.impl.Exit";
      Class<?> exitClass = systemClassLoader.loadClass(className);
      exitMethod =
          exitClass.getMethod("apply", String.class, Object[].class, Throwable.class, Object.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void initialize(Instrumentation instrumentation, boolean debug) {
    try {
      Config config = generateConfig(systemClassLoader, debug);
      AdviceConfig adviceConfig = generateAdviceConfig(systemClassLoader, config, debug);
      if (debug) {
        System.out.println("Generated Advice Config = " + adviceConfig);
      }

      SystemFlow.setServiceName(adviceConfig.getServiceName());

      AgentBuilder agentBuilder =
          new LoggingInstrumentationByteBuddyBuilder()
              .builderFromConfigWithRetransformation(adviceConfig);

      // The debugging listener shows what classes are being picked up by the instrumentation
      if (debug) {
        AgentBuilder.Listener debugListener = createDebugListener(adviceConfig.classNames());
        agentBuilder = agentBuilder.with(debugListener);
      }
      agentBuilder.installOn(instrumentation);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // The code here recapitulates the logback-config code, but in a bootstrap classloader.
  // This does mean that typesafe-config classes are pulled from bootstrap thereafter, but
  // this is pretty safe.
  public static Config generateConfig(ClassLoader classLoader, boolean debug) {
    // Look for logback.json, logback.conf, logback.properties
    Config systemProperties = ConfigFactory.systemProperties();
    String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
    Config file = ConfigFactory.empty();
    if (fileName != null) {
      file = ConfigFactory.parseFile(new File(fileName));
    }

    Config testResources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK_TEST);
    Config resources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK);
    Config reference = ConfigFactory.parseResources(classLoader, LOGBACK_REFERENCE_CONF);

    Config config =
        systemProperties // Look for a property from system properties first...
            .withFallback(file) // if we don't find it, then look in an explicitly defined file...
            .withFallback(
                testResources) // if not, then if logback-test.conf exists, look for it there...
            .withFallback(resources) // then look in logback.conf...
            .withFallback(reference) // and then finally in logback-reference.conf.
            .resolve(); // Tell config that we want to use ${?ENV_VAR} type stuff.

    // Add a check to show the config value if nothing is working...
    if (debug) {
      String configString = config.root().render(ConfigRenderOptions.defaults());
      System.out.println(configString);
    }
    return config;
  }

  public static AdviceConfig generateAdviceConfig(
      ClassLoader classLoader, Config config, boolean debug) throws Exception {
    List<AdviceConfig> configs = new ArrayList<>();

    String serviceName = config.getString("logback.bytebuddy.service-name");
    AdviceConfig adviceConfig = new AdviceConfig(classLoader, serviceName);

    Set<Map.Entry<String, ConfigValue>> entries =
        config.getConfig("logback.bytebuddy.tracing").entrySet();
    for (Map.Entry<String, ConfigValue> entry : entries) {
      String className = clean(entry.getKey());
      ConfigValue value = entry.getValue();
      if (value.valueType() == ConfigValueType.LIST) {
        List<String> methodNames =
            ((List<String>) value.unwrapped())
                .stream().map(LoggingInstrumentationAdvice::clean).collect(Collectors.toList());
        if (methodNames.size() == 1 && Objects.equals(methodNames.get(0), "*")) {
          if (debug) {
            System.out.println("Using wildcard matching for class " + className);
          }
          adviceConfig.addTrace(className);
        } else {
          adviceConfig.addTrace(className, methodNames);
        }
      } else {
        throw new IllegalStateException("unknown config!");
      }
    }
    return adviceConfig;
  }

  private static String clean(String key) {
    return key.replaceAll("\"", "").trim();
  }

  private static AgentBuilder.Listener createDebugListener(List<String> classNames) {
    return new AgentBuilder.Listener.Filtering(
        stringMatcher(classNames), AgentBuilder.Listener.StreamWriting.toSystemOut());
  }

  public static ElementMatcher.Junction<? super String> stringMatcher(
      Collection<String> typeNames) {
    boolean seen = false;
    ElementMatcher.Junction<? super String> acc = ElementMatchers.none();
    for (String typeName : typeNames) {
      StringMatcher stringMatcher = new StringMatcher(typeName, StringMatcher.Mode.EQUALS_FULLY);
      if (!seen) {
        seen = true;
        acc = stringMatcher;
      } else {
        acc = acc.or(stringMatcher);
      }
    }
    return acc;
  }

  @Advice.OnMethodEnter
  public static void enter(
      @Advice.Origin("#t|#m|#d|#s") String origin, @Advice.AllArguments Object[] allArguments)
      throws Exception {
    enterMethod.invoke(null, origin, allArguments);
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class)
  public static void exit(
      @Advice.Origin("#t|#m|#d|#s|#r") String origin,
      @Advice.AllArguments Object[] allArguments,
      @Advice.Thrown Throwable thrown,
      @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue)
      throws Exception {
    exitMethod.invoke(null, origin, allArguments, thrown, returnValue);
  }
}
