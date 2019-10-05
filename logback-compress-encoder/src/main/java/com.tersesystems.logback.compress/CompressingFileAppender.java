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

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import java.util.Set;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class CompressingFileAppender<E> extends UnsynchronizedAppenderBase<E> {

  protected Encoder<E> encoder;

  private FileAppender<E> fileAppender;

  protected boolean append = true;

  protected String fileName = null;

  private boolean prudent = false;

  private int bufferSize = 1024000;

  private String compressAlgo = CompressorStreamFactory.getGzip();

  public Encoder<E> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<E> encoder) {
    this.encoder = encoder;
  }

  public boolean isPrudent() {
    return prudent;
  }

  public void setPrudent(boolean prudent) {
    this.prudent = prudent;
  }

  public void setAppend(boolean append) {
    this.append = append;
  }

  public void setFile(String file) {
    fileName = file;
  }

  public boolean isAppend() {
    return append;
  }

  public String getFile() {
    return fileName;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public String getCompressAlgo() {
    return compressAlgo;
  }

  public void setCompressAlgo(String compressAlgo) {
    this.compressAlgo = compressAlgo;
  }

  @Override
  public void start() {
    fileAppender = new FileAppender<>();
    fileAppender.setContext(getContext());
    fileAppender.setFile(getFile());
    fileAppender.setImmediateFlush(false);
    fileAppender.setPrudent(isPrudent());
    fileAppender.setAppend(isAppend());
    fileAppender.setName(name + "-embedded-file");

    CompressingEncoder<E> compressedEncoder = createCompressingEncoder(getEncoder());
    fileAppender.setEncoder(compressedEncoder);
    fileAppender.start();

    super.start();
  }

  public void stop() {
    fileAppender.stop();
    super.stop();
  }

  @Override
  protected void append(E eventObject) {
    fileAppender.doAppend(eventObject);
  }

  protected CompressingEncoder<E> createCompressingEncoder(Encoder<E> e) {
    int bufferSize = getBufferSize();
    String compressAlgo = getCompressAlgo();

    CompressorStreamFactory factory = CompressorStreamFactory.getSingleton();
    Set<String> names = factory.getOutputStreamCompressorNames();
    if (names.contains(getCompressAlgo())) {
      try {
        return new CompressingEncoder<>(e, compressAlgo, factory, bufferSize);
      } catch (CompressorException ex) {
        throw new RuntimeException("Cannot create CompressingEncoder", ex);
      }
    } else {
      throw new RuntimeException("No such compression algorithm: " + compressAlgo);
    }
  }
}
