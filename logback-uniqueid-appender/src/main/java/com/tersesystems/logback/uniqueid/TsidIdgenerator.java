package com.tersesystems.logback.uniqueid;

import com.github.f4b6a3.tsid.TsidFactory;

/**
 * Generates a TSID according to <a
 * href="https://github.com/f4b6a3/tsid-creator">https://github.com/f4b6a3/tsid-creator</a>.
 */
public class TsidIdgenerator implements IdGenerator {

  // "tsidcreator.node" system property should be set,
  // but small hope of that happening, so choose a large node count.
  private final TsidFactory factory = TsidFactory.newInstance4096();

  @Override
  public String generateId() {
    return factory.create().toString();
  }
}
