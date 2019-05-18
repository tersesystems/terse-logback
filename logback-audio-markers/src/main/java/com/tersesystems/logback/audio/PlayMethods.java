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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public interface PlayMethods {

    default void play(InputStream inputStream) {
        try (final AudioInputStream in = getAudioInputStream(inputStream)) {
            play(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    default void play(File file) {
        try (final AudioInputStream in = getAudioInputStream(file)) {
            play(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    default void play(URL mediaURL) {
        try (final AudioInputStream in = getAudioInputStream(mediaURL)) {
            play(in);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    default void play(AudioInputStream audioInputStream) {
        try (final AudioInputStream in = audioInputStream) {
            AudioFormat baseFormat = in.getFormat();

            AudioFormat targetFormat = Utils.getOutFormat(baseFormat);

            try (final AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, in)){
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, targetFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                if (line != null) {
                    line.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP)
                            line.close();
                    });

                    line.open();
                    line.start();
                    byte[] buffer = new byte[4096];
                    int nBytesRead = 0;
                    while (nBytesRead != -1) {
                        nBytesRead = dataIn.read(buffer, 0, buffer.length);
                        if (nBytesRead != -1) {
                            line.write(buffer, 0, nBytesRead);
                        }
                    }
                    line.drain();
                    line.stop();
                }
            }
        } catch (LineUnavailableException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

}

class Utils {
    static AudioFormat getOutFormat(AudioFormat baseFormat) {
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
                16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
    }

    static void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        line.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP)
                line.close();
        });

        final byte[] buffer = new byte[4096];
        try {
            for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
                line.write(buffer, 0, n);
            }
        } finally {
            line.drain();
        }
    }
}