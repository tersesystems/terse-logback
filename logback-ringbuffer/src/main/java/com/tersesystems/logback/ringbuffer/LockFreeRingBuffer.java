package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jctools.queues.MpmcArrayQueue;

import java.util.Collection;
import java.util.Iterator;

/**
 * A ring buffer that uses JCTools MpmcArrayQueue under the hood.
 */
public class LockFreeRingBuffer extends RingBufferBase {

    // https://psy-lob-saw.blogspot.com/2015/01/mpmc-multi-multi-queue-vs-clq.html
    private MpmcArrayQueue<ILoggingEvent> queue;
    private volatile boolean started;

    @Override
    public void start() {
        queue = new MpmcArrayQueue<>(getCapacity());
        this.started = true;
    }

    @Override
    public void stop() {
        queue = null;
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<ILoggingEvent> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return queue.toArray(ts);
    }

    @Override
    public boolean add(ILoggingEvent e) {
        return queue.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return queue.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends ILoggingEvent> collection) {
        return queue.addAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return queue.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return queue.retainAll(collection);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean offer(ILoggingEvent e) {
        return queue.offer(e);
    }

    @Override
    public ILoggingEvent remove() {
        return queue.remove();
    }

    @Override
    public ILoggingEvent poll() {
        return queue.poll();
    }

    @Override
    public ILoggingEvent element() {
        return queue.element();
    }

    @Override
    public ILoggingEvent peek() {
        return queue.peek();
    }
}
