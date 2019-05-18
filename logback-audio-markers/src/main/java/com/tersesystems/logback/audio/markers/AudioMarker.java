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
package com.tersesystems.logback.audio.markers;

import com.tersesystems.logback.TerseBasicMarker;
import com.tersesystems.logback.audio.Player;
import com.tersesystems.logback.audio.SimplePlayer;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AudioMarker extends TerseBasicMarker implements Player {

    private final Player player;

    public AudioMarker(URL url, String name) {
        super(name);
        player = SimplePlayer.fromURL(url);
    }

    public AudioMarker(File file, String name) {
        super(name);
        player = SimplePlayer.fromFile(file);
    }

    public AudioMarker(InputStream inputStream, String name) {
        super(name);
        player = SimplePlayer.fromInputStream(inputStream);
    }


    public AudioMarker(Player player, String name) {
        super(name);
        this.player = player;
    }

    public void play() {
        player.play();
    }
}
