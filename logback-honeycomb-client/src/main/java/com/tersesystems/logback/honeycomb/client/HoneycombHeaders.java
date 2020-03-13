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
package com.tersesystems.logback.honeycomb.client;

public class HoneycombHeaders {
  public static String teamHeader() {
    return "X-Honeycomb-Team";
  }

  public static String eventTimeHeader() {
    return "X-Honeycomb-Event-Time";
  }

  public static String sampleRateHeader() {
    return "X-Honeycomb-Samplerate";
  }
}
