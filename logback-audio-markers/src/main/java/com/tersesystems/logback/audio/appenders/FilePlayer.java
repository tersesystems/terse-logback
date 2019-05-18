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
package com.tersesystems.logback.audio.appenders;

import ch.qos.logback.core.spi.ContextAwareBase;
import com.tersesystems.logback.audio.Player;
import com.tersesystems.logback.audio.SimplePlayer;

import java.io.File;

public class FilePlayer extends ContextAwareBase implements Player {

    private File file;

    public FilePlayer() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void play() {
        SimplePlayer.fromFile(file).play();
    }

}
