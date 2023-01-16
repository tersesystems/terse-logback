package com.tersesystems.logback.uniqueid;

import com.github.f4b6a3.ulid.UlidFactory;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates a monotonic ULID using a threadlocal random according to <a
 * href="https://github.com/f4b6a3/ulid-creator">https://github.com/f4b6a3/ulid-creator</a>.
 */
public class UlidIdGenerator implements IdGenerator {

  private Random random() {
    return ThreadLocalRandom.current();
  }

  private final UlidFactory factory = UlidFactory.newMonotonicInstance(() -> random().nextLong());

  @Override
  public String generateId() {
    return factory.create().toString();
  }
}
