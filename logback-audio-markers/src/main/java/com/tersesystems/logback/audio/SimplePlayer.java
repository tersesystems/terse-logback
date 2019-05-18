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

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class SimplePlayer implements PlayMethods, Player {

    private final AudioInputStream inputStream;

    protected SimplePlayer(AudioInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static Player fromURL(URL url) {
        try (final AudioInputStream in = getAudioInputStream(url)) {
            return new SimplePlayer(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Player fromFile(File file) {
        try (final AudioInputStream in = getAudioInputStream(file)) {
            return new SimplePlayer(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Player fromInputStream(InputStream inputStream) {
        try (final AudioInputStream in = getAudioInputStream(inputStream)) {
            return new SimplePlayer(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void play() {
        play(inputStream);
    }
}
