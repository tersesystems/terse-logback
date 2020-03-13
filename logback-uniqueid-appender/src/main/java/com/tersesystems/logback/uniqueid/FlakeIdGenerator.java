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
package com.tersesystems.logback.uniqueid;

import net.mguenther.idem.flake.Flake128S;
import net.mguenther.idem.provider.LinearTimeProvider;
import net.mguenther.idem.provider.StaticWorkerIdProvider;

public class FlakeIdGenerator implements IdGenerator {

  private static final Flake128S flake64 =
      new Flake128S(new LinearTimeProvider(), new StaticWorkerIdProvider("logback"));

  @Override
  public String generateId() {
    return flake64.nextId();
  }
}
