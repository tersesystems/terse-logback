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

package com.tersesystems.logback.classic;

import org.slf4j.Marker;

/** A very simple correlation id marker. */
public interface CorrelationIdMarker extends Marker {
  String getCorrelationId();

  static CorrelationIdMarker create(String value) {
    return new CorrelationIdBasicMarker(value);
  }
}

/** Implementation of correlation id. */
class CorrelationIdBasicMarker extends TerseBasicMarker implements CorrelationIdMarker {
  private final String value;

  public CorrelationIdBasicMarker(String value) {
    super("TS_CORRELATION_ID");
    this.value = value;
  }

  public String getCorrelationId() {
    return value;
  }
}
