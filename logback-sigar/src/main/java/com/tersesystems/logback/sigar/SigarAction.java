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
package com.tersesystems.logback.sigar;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jmx.MBeanUtil;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.lang.management.ManagementFactory;
import javax.management.*;
import kamon.sigar.*;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.jmx.SigarRegistry;
import org.xml.sax.Attributes;

// would love to do Process Table Query Language stuff in here...
public class SigarAction extends Action {

  @Override
  public void begin(InterpretationContext ic, String name, Attributes attributes)
      throws ActionException {
    try {
      SigarProvisioner.provision();
      Sigar s = new Sigar();
      // call it to make sure the library was loaded
      s.getPid();
      addInfo("sigar loaded successfully");
      registerWithJMX();
      getContext().putObject(SigarConstants.SIGAR_CTX_KEY, s);
    } catch (Throwable t) {
      addError("failed to load sigar", t);
    }
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {}

  void registerWithJMX() throws Exception {
    SigarRegistry sigarRegistry = new SigarRegistry();
    MBeanServer jmxServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = new ObjectName(sigarRegistry.getObjectName());
    if (!jmxServer.isRegistered(objectName)) {
      jmxServer.createMBean(sigarRegistry.getClass().getName(), objectName);
    }
  }

  void unregisterWithJMX() throws Exception {
    MBeanServer jmxServer = ManagementFactory.getPlatformMBeanServer();
    SigarRegistry sigarRegistry = new SigarRegistry();
    MBeanUtil.unregister(
        (LoggerContext) getContext(),
        jmxServer,
        new ObjectName(sigarRegistry.getObjectName()),
        this);
  }
}
