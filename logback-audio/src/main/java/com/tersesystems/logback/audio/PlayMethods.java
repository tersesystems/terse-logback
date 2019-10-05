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

import java.io.IOException;
import java.util.function.Supplier;
import javax.sound.sampled.*;

public interface PlayMethods {

  default void play(Supplier<AudioInputStream> supplier) {
    // https://docs.oracle.com/javase/tutorial/sound/playing.html
    try (final AudioInputStream in = supplier.get()) {
      AudioFormat baseFormat = in.getFormat();
      AudioFormat targetFormat =
          new AudioFormat(
              AudioFormat.Encoding.PCM_SIGNED,
              baseFormat.getSampleRate(),
              16,
              baseFormat.getChannels(),
              baseFormat.getChannels() * 2,
              baseFormat.getSampleRate(),
              false);
      try (final AudioInputStream dataIn = AudioSystem.getAudioInputStream(targetFormat, in)) {
        DataLine.Info info = new DataLine.Info(Clip.class, targetFormat);
        Clip clip = (Clip) AudioSystem.getLine(info);
        if (clip != null) {
          clip.addLineListener(
              event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
              });

          clip.open(dataIn);
          clip.start();
        }
      }
    } catch (LineUnavailableException | IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
