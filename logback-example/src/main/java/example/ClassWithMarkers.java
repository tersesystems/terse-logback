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
package example;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    public void doThingsWithMarker(String correlationId) {
        LogstashMarker logstashMarker = Markers.append("correlationId", correlationId);
        logger.info(logstashMarker, "log with marker explicitly");
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker(correlationId);
    }
}