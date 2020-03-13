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
package com.tersesystems.logback.bytebuddy.impl;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.tersesystems.logback.tracing.SpanInfo;
import com.tersesystems.logback.tracing.SpanMarkerFactory;
import com.tersesystems.logback.tracing.Tracer;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class SystemFlow {
  // https://github.com/qos-ch/slf4j/blob/master/slf4j-ext/src/main/java/org/slf4j/ext/XLogger.java#L44
  public static final Marker FLOW_MARKER = MarkerFactory.getMarker("FLOW");
  public static final Marker ENTRY_MARKER = MarkerFactory.getMarker("ENTRY");
  public static final Marker EXIT_MARKER = MarkerFactory.getMarker("EXIT");
  public static final Marker EXCEPTION_MARKER = MarkerFactory.getMarker("EXCEPTION");
  public static final Marker THROWING_MARKER = MarkerFactory.getMarker("THROWING");

  private static final SpanMarkerFactory markerFactory = new SpanMarkerFactory();

  static {
    ENTRY_MARKER.add(FLOW_MARKER);
    EXIT_MARKER.add(FLOW_MARKER);
    THROWING_MARKER.add(EXCEPTION_MARKER);
  }

  private static String serviceName;
  private static Supplier<String> idGenerator;
  private static SafeArguments safeArguments = new SafeArguments();

  static {
    // The out of the box UUID.randomUUID() is synchronized, which will block threads and
    // generally gum things up.  The faster XML one is better, but still need to benchmark
    // considering how tracing can be injected anywhere.
    RandomBasedGenerator uuidGenerator = new RandomBasedGenerator(null);
    SystemFlow.setIdGenerator(() -> uuidGenerator.generate().toString());
  }

  private static LoggerResolver loggerResolver =
      new DeclaringTypeLoggerResolver(LoggerFactory::getILoggerFactory);

  public static LoggerResolver getLoggerResolver() {
    return loggerResolver;
  }

  public static void setLoggerResolver(LoggerResolver loggerResolver) {
    SystemFlow.loggerResolver = loggerResolver;
  }

  public static Logger getLogger(String origin) {
    return loggerResolver.resolve(origin);
  }

  public static void setServiceName(String serviceName) {
    SystemFlow.serviceName = serviceName;
  }

  public static void setIdGenerator(Supplier<String> idGenerator) {
    SystemFlow.idGenerator = idGenerator;
  }

  public static SafeArguments getSafeArguments() {
    return safeArguments;
  }

  public static void setSafeArguments(SafeArguments safeArguments) {
    SystemFlow.safeArguments = safeArguments;
  }

  public static LogstashMarker createMarker(SpanInfo span) {
    return baseMarkers().and(markerFactory.create(span));
  }

  public static void pushSpan(String name) {
    Tracer.pushSpan(name, serviceName, idGenerator);
  }

  public static Optional<SpanInfo> popSpan() {
    return Tracer.popSpan();
  }

  static StructuredArgument safeReturnValue(Object returnValue) {
    String safeReturnValue = safeArguments.apply(returnValue);
    return kv("return_value", safeReturnValue);
  }

  static StructuredArgument safeArguments(Object[] allArguments) {
    List<String> safeArgs = safeArguments.apply(allArguments);
    return kv("arguments", safeArgs);
  }

  static String createName(String className, String method, String signature) {
    return className + "." + method + signature;
  }

  static LogstashMarker baseMarkers() {
    //        Thread t = Thread.currentThread();
    //        //LogstashMarker threadNameMarker = append("trace.thread_name", t.getName());
    //        LogstashMarker threadIdMarker = append("thread_id", t.getId());
    //        return threadIdMarker.and(threadIdMarker);
    return Markers.empty();
  }
}
