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
package com.tersesystems.logback.audio;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePlayer extends ContextAwareBase implements Player, LifeCycle {

  private String file;
  private Path path;
  private volatile boolean started = false;

  public FilePlayer() {}

  public void setFile(String file) {
    this.file = file;
  }

  @Override
  public void play() {
    SimplePlayer.fromPath(path).play();
  }

  @Override
  public void start() {
    path = Paths.get(file);
    if (Files.exists(path)) {
      started = true;
    } else {
      addError(String.format("Path %s does not exist!", path));
      started = false;
    }
  }

  @Override
  public void stop() {
    path = null;
    started = false;
  }

  @Override
  public boolean isStarted() {
    return started;
  }
}
