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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Supplier;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class SimplePlayer implements PlayMethods, Player {

    private final Supplier<AudioInputStream> supplier;

    protected SimplePlayer(Supplier<AudioInputStream> supplier) {
        this.supplier = supplier;
    }

    public static Player fromURL(URL url) {
        return new SimplePlayer(() -> {
            try {
                return getAudioInputStream(url);
            } catch (UnsupportedAudioFileException | IOException e) {
                throw new PlayerException(e);
            }
        });
    }

    public static Player fromPath(Path path) {
        return new SimplePlayer(() -> {
            try {
                return getAudioInputStream(path.toFile());
            } catch (UnsupportedAudioFileException | IOException e) {
                throw new PlayerException(e);
            }
        });
    }

    public static Player fromInputStream(InputStream inputStream) {
        return new SimplePlayer(() -> {
            try {
                return getAudioInputStream(inputStream);
            } catch (UnsupportedAudioFileException | IOException e) {
                throw new PlayerException(e);
            }
        });
    }

    @Override
    public void play() {
        play(supplier);
    }
}
