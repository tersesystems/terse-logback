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
package com.tersesystems.logback.classic;

import org.slf4j.Marker;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * A marker that can be extended with custom behavior.
 *
 * Following on from logstash-logback-marker
 */
public class TerseBasicMarker implements Marker {

    private final String name;
    private List<Marker> referenceList;

    public TerseBasicMarker(String name) {
        requireNonNull(name, "A marker name cannot be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized void add(Marker reference) {
        requireNonNull(reference, "A null value cannot be added to a Marker as reference.");

        if (!(this.contains(reference) || reference.contains(this))) {
            if (referenceList == null) {
                referenceList = new Vector<>();
            }
            referenceList.add(reference);
        }
    }

    public synchronized boolean hasReferences() {
        return referenceList != null && referenceList.size() > 0;
    }

    /**
     * @deprecated Replaced by {@link #hasReferences()}.
     */
    @Deprecated
    public boolean hasChildren() {
        return hasReferences();
    }

    public synchronized Iterator<Marker> iterator() {
        return hasReferences() ? referenceList.iterator() : Collections.emptyIterator();
    }

    public synchronized boolean remove(Marker referenceToRemove) {
        if (hasReferences()) {
            return referenceList.remove(referenceToRemove);
        } else {
            return false;
        }
    }

    public boolean contains(Marker other) {
        requireNonNull(other, "other cannot be null");

        if (this.equals(other)) {
            return true;
        } else if (hasReferences()) {
            return referenceList.stream().anyMatch(ref -> ref.contains(other));
        } else {
            return false;
        }
    }

    public boolean contains(String name) {
        requireNonNull(name, "name cannot be null");

        if (this.name.equals(name)) {
            return true;
        } else if (hasReferences()) {
            return referenceList.stream().anyMatch(ref -> ref.contains(name));
        } else {
            return false;
        }
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