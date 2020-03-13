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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class AudioAppender extends AppenderBase<ILoggingEvent> implements PlayerAttachable {

  private Player player;

  @Override
  protected void append(ILoggingEvent eventObject) {
    player.play();
  }

  @Override
  public void addPlayer(Player player) {
    this.player = player;
  }

  @Override
  public void clearAllPlayers() {
    this.player = null;
  }

  @Override
  public void start() {
    if (player == null) {
      addError("No player found!");
    } else {
      super.start();
    }
  }
}
