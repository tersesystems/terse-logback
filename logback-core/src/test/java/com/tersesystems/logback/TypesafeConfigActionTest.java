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
package com.tersesystems.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.Interpreter;
import com.typesafe.config.Config;
import org.junit.Test;
import org.xml.sax.Attributes;

import java.util.HashMap;

import static com.tersesystems.logback.TypesafeConfigAction.LOGBACK_DEBUG_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

public class TypesafeConfigActionTest {


    static class DummyAttributes implements Attributes {

        HashMap<String, String> atts = new HashMap<String, String>();

        public int getIndex(String qName) {
            return 0;
        }

        public int getIndex(String uri, String localName) {
            return 0;
        }

        public int getLength() {
            return 0;
        }

        public String getLocalName(int index) {
            return null;
        }

        public String getQName(int index) {
            return null;
        }

        public String getType(int index) {
            return null;
        }

        public String getType(String qName) {
            return null;
        }

        public String getType(String uri, String localName) {
            return null;
        }

        public String getURI(int index) {
            return null;
        }

        public String getValue(int index) {
            return null;
        }

        public String getValue(String qName) {
            return atts.get(qName);
        }

        public void setValue(String key, String value) {
            atts.put(key, value);
        }

        public String getValue(String uri, String localName) {
            return null;
        }

    }

    @Test
    public void testConfigActionWithContext() throws ActionException {
        TypesafeConfigAction action = new TypesafeConfigAction();

        System.setProperty(LOGBACK_DEBUG_PROPERTY, "true");
        Context lc = new LoggerContext();
        DummyAttributes atts = new DummyAttributes();

        Interpreter interpreter = null;
        InterpretationContext interpretationContext = new InterpretationContext(lc, interpreter);

        atts.setValue("scope", "context");
        action.begin(interpretationContext, "typesafeConfig", atts);

        String fooValue = lc.getProperty("foo");
        assertThat(fooValue).isEqualTo("bar");
    }

    @Test
    public void testConfigActionWithLocal() throws ActionException {
        TypesafeConfigAction action = new TypesafeConfigAction();

        System.setProperty(LOGBACK_DEBUG_PROPERTY, "true");
        Context lc = new LoggerContext();
        DummyAttributes atts = new DummyAttributes();

        Interpreter interpreter = null;
        InterpretationContext interpretationContext = new InterpretationContext(lc, interpreter);

        action.begin(interpretationContext, "typesafeConfig", null);

        String fooValue = interpretationContext.getProperty("foo");
        assertThat(fooValue).isEqualTo("bar");
    }
}
