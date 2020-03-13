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
package com.tersesystems.logback.compress;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.EncoderBase;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class CompressingEncoder<E> extends EncoderBase<E> {
  private final Accumulator accumulator;
  private final Encoder<E> encoder;

  public CompressingEncoder(
      Encoder<E> encoder, String compressAlgo, CompressorStreamFactory factory, int bufferSize)
      throws CompressorException {
    this.encoder = encoder;
    this.accumulator = new Accumulator(compressAlgo, factory, bufferSize);
  }

  @Override
  public byte[] headerBytes() {
    try {
      return accumulator.apply(encoder.headerBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] encode(E event) {
    try {
      return accumulator.apply(encoder.encode(event));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] footerBytes() {
    try {
      return accumulator.drain(encoder.footerBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static class Accumulator {
    private final ByteArrayOutputStream byteOutputStream;
    private final CompressorOutputStream stream;
    private final LongAdder count = new LongAdder();
    private final int bufferSize;

    public Accumulator(String compressAlgo, CompressorStreamFactory factory, int bufferSize)
        throws CompressorException {
      this.bufferSize = bufferSize;
      this.byteOutputStream = new ByteArrayOutputStream();
      this.stream = factory.createCompressorOutputStream(compressAlgo, byteOutputStream);
    }

    boolean isFlushable() {
      return count.intValue() >= bufferSize;
    }

    byte[] apply(byte[] bytes) throws IOException {
      count.add(bytes.length);
      stream.write(bytes);

      if (isFlushable()) {
        stream.flush();
        byte[] output = byteOutputStream.toByteArray();
        byteOutputStream.reset();
        count.reset();
        return output;
      } else {
        return new byte[0];
      }
    }

    byte[] drain(byte[] inputBytes) throws IOException {
      if (inputBytes != null) {
        stream.write(inputBytes);
      }
      stream.close();
      count.reset();
      return byteOutputStream.toByteArray();
    }
  }
}
