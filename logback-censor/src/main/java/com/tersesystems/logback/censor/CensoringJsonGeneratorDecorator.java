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
package com.tersesystems.logback.censor;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.filter.FilteringGeneratorDelegate;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import net.logstash.logback.decorate.JsonGeneratorDecorator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// https://github.com/FasterXML/jackson-core/issues/185
public class CensoringJsonGeneratorDecorator extends ContextAwareBase implements CensorAttachable, JsonGeneratorDecorator, LifeCycle {

    private Censor censor;
    private boolean started;

    @Override
    public JsonGenerator decorate(JsonGenerator generator) {
        CensoringJsonGeneratorDelegate substitutionDelegate = new CensoringJsonGeneratorDelegate(generator);
        return new FilteringGeneratorDelegate(substitutionDelegate, new CensoringTokenFilter(), true, true);
    }

    public Censor getCensor() {
        return censor;
    }

    @Override
    public void setCensor(Censor censor) {
        this.censor = censor;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        filterKeys.clear();
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    private List<String> filterKeys = new ArrayList<>();

    public void addFilterKey(String filterKey) {
        this.filterKeys.add(filterKey);
    }

    // Removes entire value attached to the key.
    private class CensoringTokenFilter extends TokenFilter {
        @Override
        public TokenFilter includeElement(int index) {
            return this;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            if (shouldFilter(name)) {
                return null;
            }
            return TokenFilter.INCLUDE_ALL;
        }

        private boolean shouldFilter(String name) {
            return filterKeys != null && filterKeys.contains(name);
        }

        @Override
        protected boolean _includeScalar() { return false; }
    }

    // Filters text inside JSON
    private class CensoringJsonGeneratorDelegate extends JsonGeneratorDelegate {
        public CensoringJsonGeneratorDelegate(JsonGenerator d) {
            super(d);
        }

        private String censorSensitiveMessage(String original) {
            if (censor != null) {
                return String.valueOf(censor.censorText(original));
            } else {
                return original;
            }
        }

        @Override
        public void writeString(String original) throws IOException
        {
            final String value = censorSensitiveMessage(original);
            delegate.writeString(value);
        }

        @Override
        public void writeString(char[] text, int offset, int len) throws IOException
        {
            final String original = new String(text, offset, len);
            final String value = censorSensitiveMessage(original);
            delegate.writeString(value);
        }

        @Override
        public void writeString(SerializableString serializableString) throws IOException
        {
            final String original = serializableString.getValue();
            final String value = censorSensitiveMessage(original);
            delegate.writeString(value);
        }

        @Override
        public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException
        {
            String original = new String(text, offset, length, StandardCharsets.UTF_8);
            final String value = censorSensitiveMessage(original);
            delegate.writeString(value);
        }

        @Override
        public void writeUTF8String(byte[] text, int offset, int length) throws IOException
        {
            String original = new String(text, offset, length, StandardCharsets.UTF_8);
            final String value = censorSensitiveMessage(original);
            delegate.writeString(value);
        }
    }

}
