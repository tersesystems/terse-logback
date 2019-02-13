package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.*;

// https://dzone.com/articles/limiting-repetitive-log-messages-with-logback
// From https://github.com/mahanhz/clean-architecture-example/blob/master/application/configuration/src/main/java/com/example/clean/app/configuration/log/ExpiringDuplicateMessageFilter.java
public class ExpiringDuplicateMessageFilter extends TurboFilter {

    private static final int MAX_KEY_LENGTH                     = 100;
    private static final int DEFAULT_CACHE_SIZE                 = 100;
    private static final int DEFAULT_ALLOWED_REPETITIONS        = 5;
    private static final int DEFAULT_EXPIRE_AFTER_WRITE_SECONDS = 60;

    private int          allowedRepetitions      = DEFAULT_ALLOWED_REPETITIONS;
    private int          cacheSize               = DEFAULT_CACHE_SIZE;
    private int          expireAfterWriteSeconds = DEFAULT_EXPIRE_AFTER_WRITE_SECONDS;
    private String       excludeMarkers          = "";

    private List<Marker> excludeMarkersList      = new ArrayList<>();

    private Cache<String, Integer> msgCache;

    @Override
    public void start() {
        msgCache = buildCache();
        excludeMarkersList = excludeMarkers(excludeMarkers);

        super.start();
    }

    @Override
    public void stop() {
        msgCache.invalidateAll();
        msgCache = null;

        super.stop();
    }

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level,
                              final String format, final Object[] params, final Throwable t) {
        if (excludeMarkersList.contains(marker)) {
            return FilterReply.NEUTRAL;
        }

        int count = 0;

        if (isNotBlank(format)) {
            final String key = abbreviate(format + paramsAsString(params, logger), MAX_KEY_LENGTH);

            final Integer msgCount = msgCache.getIfPresent(key);

            if (msgCount != null) {
                count = msgCount + 1;
            }

            msgCache.put(key, count);
        }

        return (count <= allowedRepetitions) ? FilterReply.NEUTRAL : FilterReply.DENY;
    }

    public int getAllowedRepetitions() {
        return allowedRepetitions;
    }

    public void setAllowedRepetitions(final int allowedRepetitions) {
        this.allowedRepetitions = allowedRepetitions;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public int getExpireAfterWriteSeconds() {
        return expireAfterWriteSeconds;
    }

    public void setExpireAfterWriteSeconds(final int expireAfterWriteSeconds) {
        this.expireAfterWriteSeconds = expireAfterWriteSeconds;
    }

    public String getExcludeMarkers() {
        return excludeMarkers;
    }

    public void setExcludeMarkers(final String excludeMarkers) {
        this.excludeMarkers = excludeMarkers;
    }

    private List<Marker> excludeMarkers(final String markersToExclude) {
        final List<String> listOfMarkers = Arrays.asList(markersToExclude.split("\\s*,\\s*"));
        return listOfMarkers.stream()
                .map(MarkerFactory::getMarker)
                .collect(toList());
    }

    private String paramsAsString(final Object[] params, final Logger logger) {
        if (params != null && startsWith(logger.getName(), "com.example")) {
            return Arrays.stream(params).map(Object::toString).collect(joining("_"));
        }

        return "";
    }

    private Cache<String, Integer> buildCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(expireAfterWriteSeconds, TimeUnit.SECONDS)
                .initialCapacity(cacheSize)
                .maximumSize(cacheSize)
                .build();
    }
}
