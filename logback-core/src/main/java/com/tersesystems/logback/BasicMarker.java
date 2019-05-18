package com.tersesystems.logback;

import org.slf4j.Marker;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * A marker that can be extended with custom behavior.
 *
 * Following on from logstash-logback-marker
 */
public class BasicMarker implements Marker {

    private final String name;
    private List<Marker> referenceList;

    public BasicMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized void add(Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException(
                "A null value cannot be added to a Marker as reference.");
        }

        // no point in adding the reference multiple times
        // avoid recursion
        // a potential reference should not its future "parent" as a reference
        if (!(this.contains(reference) || reference.contains(this))) {
            // let's add the reference
            if (referenceList == null) {
                referenceList = new Vector<Marker>();
            }
            referenceList.add(reference);
        }
    }

    public synchronized boolean hasReferences() {
        return ((referenceList != null) && (referenceList.size() > 0));
    }

    public boolean hasChildren() {
        return hasReferences();
    }

    public synchronized Iterator<Marker> iterator() {
        return referenceList != null ? referenceList.iterator() : Collections.emptyIterator();
    }

    public synchronized boolean remove(Marker referenceToRemove) {
        if (referenceList != null) {
            for (int i = 0; i < referenceList.size(); i++) {
                Marker m = referenceList.get(i);
                if (referenceToRemove.equals(m)) {
                    referenceList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contains(Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }

        if (this.equals(other)) {
            return true;
        }

        if (hasReferences()) {
            for (Marker ref : referenceList) {
                if (ref.contains(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is mainly used with Expression Evaluators.
     */
    public boolean contains(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }

        if (this.name.equals(name)) {
            return true;
        }

        if (hasReferences()) {
            for (Marker ref : referenceList) {
                if (ref.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final String OPEN = "[ ";
    private static final String CLOSE = " ]";
    private static final String SEP = ", ";

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Marker))
            return false;

        final Marker other = (Marker) obj;
        return name.equals(other.getName());
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        if (!this.hasReferences()) {
            return this.getName();
        }
        Iterator<Marker> it = this.iterator();
        Marker reference;
        StringBuilder sb = new StringBuilder(this.getName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            reference = it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
                sb.append(SEP);
            }
        }
        sb.append(CLOSE);

        return sb.toString();
    }
}