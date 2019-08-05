package com.tersesystems.logback.core;

import ch.qos.logback.core.read.CyclicBufferAppender;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Collections {

    public static <E> Stream<E> fromIterator(Iterator<E> iterator) {
        Spliterator<E> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false);
    }

    public static <E> Iterator<E> fromCyclicAppender(CyclicBufferAppender<E> cyclic) {
        int length = cyclic.getLength();
        return new Iterator<E>() {
            private volatile AtomicInteger i = new AtomicInteger(0);

            @Override
            public boolean hasNext() {
                return i.get() < cyclic.getLength();
            }

            @Override
            public E next() {
                return cyclic.get(i.getAndIncrement());
            }
        };
    }

}
