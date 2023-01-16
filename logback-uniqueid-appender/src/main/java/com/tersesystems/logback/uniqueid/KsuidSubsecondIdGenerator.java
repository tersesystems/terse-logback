package com.tersesystems.logback.uniqueid;

import com.github.f4b6a3.ksuid.*;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates a subsecond KSUID according to <a
 * href="https://github.com/f4b6a3/ksuid-creator">https://github.com/f4b6a3/ksuid-creator</a>.
 */
public class KsuidSubsecondIdGenerator implements IdGenerator {

  private Random random() {
    return ThreadLocalRandom.current();
  }

  private final KsuidFactory factory = KsuidFactory.newSubsecondInstance(() -> random().nextLong());

  @Override
  public String generateId() {
    return factory.create().toString();
  }
}
