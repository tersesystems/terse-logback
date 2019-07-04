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
package com.tersesystems.logback.exceptionmapping;

import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventException;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import java.io.InterruptedIOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionMappingTest {

    @Test
    public void testSimpleArgument() {
        Consumer<Exception> reporter = Throwable::printStackTrace;
        DefaultExceptionMappingRegistry mappings = new DefaultExceptionMappingRegistry(reporter);
        populate(mappings);

        Exception ex = new EventException(EventException.UNSPECIFIED_EVENT_TYPE_ERR, "unspecified");
        List<ExceptionProperty> args = mappings.apply(ex);

        assertThat(args.get(0)).isEqualTo(ExceptionProperty.create("code", EventException.UNSPECIFIED_EVENT_TYPE_ERR));
    }

    @Test
    public void testComplexArgument() {
        Consumer<Exception> reporter = Throwable::printStackTrace;
        DefaultExceptionMappingRegistry mappings = new DefaultExceptionMappingRegistry(reporter);
        populate(mappings);

        String reason = "felt like it";
        String SQLState = "SELECT reason FROM possible_excuses";
        int vendorCode = 1337;
        int[] updateCounts = { 1 };
        Throwable cause = new SQLException("cause of exception");

        Exception ex = new BatchUpdateException(reason, SQLState, vendorCode, updateCounts, cause);
        List<ExceptionProperty> args = mappings.apply(ex);

        // combination of BatchUpdateException + SQLException arguments
        assertThat(args.get(0)).isEqualTo(ExceptionProperty.create("updateCounts", updateCounts));
        assertThat(args.get(1)).isEqualTo(ExceptionProperty.create("errorCode", 1337));
        assertThat(args.get(2)).isEqualTo(ExceptionProperty.create("SQLState", SQLState));
    }

    private void populate(DefaultExceptionMappingRegistry mappings) {
        mappings.register(BatchUpdateException.class, "updateCounts");
        mappings.register(SQLException.class, "errorCode", "SQLState");
        mappings.register(EventException.class, (e -> singletonList(ExceptionProperty.create("code", e.code))));
        mappings.register(XMLStreamException.class, e -> {
            Location l = e.getLocation();
            if (l == null) {
                return Collections.emptyList();
            }
            return asList(
                    ExceptionProperty.create("lineNumber", l.getLineNumber()),
                    ExceptionProperty.create("columnNumber", l.getColumnNumber()),
                    ExceptionProperty.create("systemId", l.getSystemId()),
                    ExceptionProperty.create("publicId", l.getPublicId()),
                    ExceptionProperty.create("characterOffset", l.getCharacterOffset())
            );
        });
        mappings.register(InterruptedIOException.class, e -> singletonList(ExceptionProperty.create("bytesTransferred", e.bytesTransferred)));
        mappings.register(DOMException.class, (e -> singletonList(ExceptionProperty.create("code", e.code))));
    }

}
