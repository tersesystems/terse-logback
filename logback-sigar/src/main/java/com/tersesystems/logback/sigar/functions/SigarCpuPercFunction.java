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
package com.tersesystems.logback.sigar.functions;

import com.tersesystems.logback.sigar.SigarRuntimeException;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarCpuPercFunction implements SigarFunction<CpuPerc> {
  @Override
  public CpuPerc apply(Sigar sigar) {
    try {
      return sigar.getCpuPerc();
    } catch (SigarException e) {
      throw new SigarRuntimeException(e);
    }
  }
}
