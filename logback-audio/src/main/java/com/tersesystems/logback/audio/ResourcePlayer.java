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
package com.tersesystems.logback.audio;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.net.URL;

public class ResourcePlayer extends ContextAwareBase implements Player, LifeCycle {

  private String resource;
  private URL resourceURL;
  private volatile boolean started = false;

  public ResourcePlayer() {}

  public void setResource(String resource) {
    this.resource = resource;
  }

  @Override
  public void play() {
    try {
      SimplePlayer.fromURL(resourceURL).play();
    } catch (Exception e) {
      addError(String.format("Cannot play resource %s", resourceURL), e);
    }
  }

  @Override
  public void start() {
    resourceURL = getClass().getResource(resource);
    if (resourceURL != null) {
      started = true;
    } else {
      addError(String.format("Resource %s does not exist!", resource));
      started = false;
    }
  }

  @Override
  public void stop() {
    resource = null;
    started = false;
  }

  @Override
  public boolean isStarted() {
    return started;
  }
}
