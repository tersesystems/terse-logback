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

import com.github.f4b6a3.uuid.factory.rfc4122.RandomBasedFactory;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates a Random UUIDv4 using a ThreadLocalRandom from <a
 * href="https://github.com/f4b6a3/uuid-creator">https://github.com/f4b6a3/uuid-creator</a>
 */
public class RandomUUIDIdGenerator implements IdGenerator {
  private Random random() {
    return ThreadLocalRandom.current();
  }

  private final RandomBasedFactory factory = new RandomBasedFactory(() -> random().nextLong());

  @Override
  public String generateId() {
    return factory.create().toString();
  }
}
