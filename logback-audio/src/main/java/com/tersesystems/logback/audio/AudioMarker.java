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

import com.tersesystems.logback.TerseBasicMarker;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class AudioMarker extends TerseBasicMarker implements Player {

    private static final String MARKER_NAME = "TS_AUDIO_MARKER";

    private final Player player;

    public AudioMarker(URL url) {
        super(MARKER_NAME);
        player = SimplePlayer.fromURL(url);
    }

    public AudioMarker(Path path) {
        super(MARKER_NAME);
        player = SimplePlayer.fromPath(path);
    }

    public AudioMarker(InputStream inputStream) {
        super(MARKER_NAME);
        player = SimplePlayer.fromInputStream(inputStream);
    }

    public AudioMarker(Player player) {
        super(MARKER_NAME);
        this.player = player;
    }

    public void play() {
        player.play();
    }
}
