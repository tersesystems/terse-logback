package com.tersesystems.logback.context;

import org.slf4j.Marker;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

// Immutable class, create multiple contexts and use "and" to aggregate more context.
public abstract class AbstractContext<M extends Marker, THIS extends Context<M, THIS>> implements Context<M, THIS> {

    private final Map<?, ?> entries;

    protected AbstractContext(Map<?, ?> entries) {
        this.entries = entries;
    }

    /**
     * @return an unmodifiable map of entries.
     */
    public Map<?, ?> entries() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public String toString() {
        String result = entries.entrySet().stream().map(entry ->
                        String.join("=",
                                entry.getKey().toString(),
                                entry.getValue().toString()))
                .collect(Collectors.joining(","));
        return this.getClass().getSimpleName() + "(" + result + ")";
    }
}
