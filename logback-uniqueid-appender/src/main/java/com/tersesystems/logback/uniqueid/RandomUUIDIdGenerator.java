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

import com.fasterxml.uuid.impl.RandomBasedGenerator;

public class RandomUUIDIdGenerator implements IdGenerator {

  // Using java.util.UUID.fromRandom() has thread contention issues due to synchronized block.
  private static final RandomBasedGenerator idgen = new RandomBasedGenerator(null);

  @Override
  public String generateId() {
    return idgen.generate().toString();
  }
}
