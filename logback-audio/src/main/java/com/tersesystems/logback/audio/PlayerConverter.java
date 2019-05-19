package com.tersesystems.logback.audio;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Marker;

import java.util.Iterator;

public class PlayerConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        writePlayerMarkerIfNecessary(event.getMarker());
        return null;
    }

    private void writePlayerMarkerIfNecessary(Marker marker) {
        if (marker != null) {
            if (isPlayerMarker(marker)) {
                ((Player) marker).play();
            }

            if (marker.hasReferences()) {
                for (Iterator<Marker> i = marker.iterator(); i.hasNext();) {
                    writePlayerMarkerIfNecessary(i.next());
                }
            }
        }
    }

    private static boolean isPlayerMarker(Marker marker) {
        return marker instanceof Player;
    }

}
