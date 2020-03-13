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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.sigar.functions.SigarCpuPercFunction;
import java.util.Objects;
import org.hyperic.sigar.CpuPerc;

public class CPUPercentageConverter extends ClassicConverter implements SigarContextAware {

  @Override
  public String convert(ILoggingEvent event) {
    SigarCpuPercFunction fn = new SigarCpuPercFunction();
    return getSigar()
        .map(fn)
        .map(this::format)
        .orElseGet(
            () -> {
              addError("Sigar is not registered!");
              return "";
            });
  }

  private String format(CpuPerc cpu) {
    String firstOption = getFirstOption();
    if (Objects.equals(firstOption, "sys")) {
      return sys(cpu);
    }
    if (Objects.equals(firstOption, "user")) {
      return user(cpu);
    } else {
      return sys(cpu) + " " + user(cpu);
    }
  }

  // Attempt logfmt https://www.brandur.org/logfmt
  private String sys(CpuPerc cpu) {
    return "sys=" + cpu.getSys();
  }

  private String user(CpuPerc cpu) {
    return "user=" + cpu.getUser();
  }
}
