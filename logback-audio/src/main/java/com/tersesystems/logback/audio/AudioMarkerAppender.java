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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.Iterator;
import org.slf4j.Marker;

public class AudioMarkerAppender extends AppenderBase<ILoggingEvent> {

  @Override
  protected void append(ILoggingEvent eventObject) {
    writePlayerMarkerIfNecessary(eventObject.getMarker());
  }

  private void writePlayerMarkerIfNecessary(Marker marker) {
    if (marker != null) {
      if (isPlayerMarker(marker)) {
        ((Player) marker).play();
      }

      if (marker.hasReferences()) {
        for (Iterator<Marker> i = marker.iterator(); i.hasNext(); ) {
          writePlayerMarkerIfNecessary(i.next());
        }
      }
    }
  }

  private static boolean isPlayerMarker(Marker marker) {
    return marker instanceof Player;
  }
}
