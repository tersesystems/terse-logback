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
package com.tersesystems.logback.core;

import org.slf4j.Marker;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtils {

    /**
     * Returns a stream containing the marker itself and the iterator belonging to the marker.
     *
     * @param marker the marker
     * @return empty stream if marker is null, stream.of(marker) if no references, otherwise Stream.of(marker, references)
     */
    @SuppressWarnings("unchecked")
    public static Stream<Marker> fromMarker(Marker marker) {
        if (marker == null) {
            return Stream.empty();
        }
        if (! marker.hasReferences()) {
            return Stream.of(marker);
        }
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(marker.iterator(), 0);
        return (Stream<Marker>) Stream.concat(Stream.of(marker), StreamSupport.stream(spliterator, false));
    }

    public static <E> Stream<E> fromIterator(Iterator<E> iterator) {
        Spliterator<E> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false);
    }


}
