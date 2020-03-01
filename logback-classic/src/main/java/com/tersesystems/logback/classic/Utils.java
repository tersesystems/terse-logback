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
package com.tersesystems.logback.classic;

import static java.util.Objects.requireNonNull;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.tersesystems.logback.classic.functional.GetAppenderFunction;
import com.tersesystems.logback.classic.functional.RootLoggerSupplier;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

public class Utils {
  private final LoggerContext loggerContext;

  Utils(LoggerContext loggerContext) {
    this.loggerContext = requireNonNull(loggerContext);
  }

  public static LoggerContext defaultContext() {
    ContextSelectorStaticBinder singleton = ContextSelectorStaticBinder.getSingleton();
    if (singleton != null && singleton.getContextSelector() != null) {
      return singleton.getContextSelector().getLoggerContext();
    } else {
      ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
      return (LoggerContext) loggerFactory;
    }
  }

  public static LoggerContext contextFromResource(String resourcePath) throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = requireNonNull(Utils.class.getResource(requireNonNull(resourcePath)));
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  public static Utils create(LoggerContext loggerContext) {
    return new Utils(loggerContext);
  }

  public static Utils create(String resourcePath) throws JoranException {
    return new Utils(contextFromResource(resourcePath));
  }

  public static Utils create() {
    return new Utils(defaultContext());
  }

  public List<Status> getStatusList() {
    StatusManager statusManager = getLoggerContext().getStatusManager();
    return statusManager.getCopyOfStatusList();
  }

  public LoggerContext getLoggerContext() {
    return loggerContext;
  }

  public Logger getRootLogger() {
    return RootLoggerSupplier.create(loggerContext).get();
  }

  public Logger getLogger(String loggerName) {
    return (loggerContext.getLogger(loggerName));
  }

  public Logger getLogger(Class<?> clazz) {
    return (loggerContext.getLogger(clazz));
  }

  public <E> Optional<E> getObject(Class<E> classType, String name) {
    return Optional.ofNullable(loggerContext.getObject(name))
        .filter(tf -> classType.isAssignableFrom(tf.getClass()))
        .map(classType::cast);
  }

  public <E extends TurboFilter> Optional<E> getTurboFilter(
      Class<E> classType, String turboFilterName) {
    return loggerContext.getTurboFilterList().stream()
        .filter(tf -> tf.getName().equals(turboFilterName))
        .filter(tf -> classType.isAssignableFrom(tf.getClass()))
        .map(classType::cast)
        .findFirst();
  }

  public <E extends Appender<ILoggingEvent>> Optional<E> getAppender(String appenderName) {
    return GetAppenderFunction.<E>create(loggerContext).apply(appenderName);
  }

  public Map<String, String> getMDCPropertyMap() {
    MDCAdapter mdc = MDC.getMDCAdapter();

    Map<String, String> mdcPropertyMap;
    if (mdc instanceof LogbackMDCAdapter)
      mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
    else mdcPropertyMap = mdc.getCopyOfContextMap();

    // mdcPropertyMap still null, use emptyMap()
    if (mdcPropertyMap == null) mdcPropertyMap = Collections.emptyMap();

    return mdcPropertyMap;
  }

  public LoggingEventFactory getLoggingEventFactory() {
    return new LoggingEventFactory();
  }
}
