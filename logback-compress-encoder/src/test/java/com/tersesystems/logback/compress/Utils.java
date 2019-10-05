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
package com.tersesystems.logback.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

  public static byte[] readAllBytes(InputStream inputStream) throws IOException {
    final int bufLen = 4 * 0x400; // 4KB
    byte[] buf = new byte[bufLen];
    int readLen;
    IOException exception = null;

    try {
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
          outputStream.write(buf, 0, readLen);

        return outputStream.toByteArray();
      }
    } catch (IOException e) {
      exception = e;
      throw e;
    } finally {
      if (exception == null) inputStream.close();
      else
        try {
          inputStream.close();
        } catch (IOException e) {
          exception.addSuppressed(e);
        }
    }
  }
}
