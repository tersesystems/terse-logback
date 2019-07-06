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

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.tersesystems.logback.exceptionmapping.Constants.*;
import static java.lang.String.*;
import static java.util.Arrays.asList;


public class ExceptionMappingRegistryAction extends Action {

    ExceptionMappingRegistry mappingsRegistry;
    private boolean inError = false;
    private final Consumer<Exception> handler = e -> addError("Cannot map exception!", e);

    @SuppressWarnings("unchecked")
    public void begin(InterpretationContext ic, String localName, Attributes attributes) throws ActionException {
        mappingsRegistry = null;
        inError = false;

        Context context = getContext();
        HashMap<String, ExceptionMappingRegistry> mappingsBag = (HashMap<String, ExceptionMappingRegistry>) context.getObject(REGISTRY_BAG);
        if (mappingsBag == null) {
            mappingsBag = new HashMap<>();
            context.putObject(REGISTRY_BAG, mappingsBag);
        }

        try {
            mappingsRegistry = new DefaultExceptionMappingRegistry(handler);
            initializeRegistry(mappingsRegistry);
            
            String mappingsName = ic.subst(attributes.getValue(NAME_ATTRIBUTE));
            if (OptionHelper.isEmpty(mappingsName)) {
                addInfo(format("No mappingsRegistry name given for mappingsRegistry, using default \"%s\"", DEFAULT_MAPPINGS_KEY));
                mappingsName = DEFAULT_MAPPINGS_KEY;
            } else {
                addInfo("Naming mappingsRegistry as [" + mappingsName + "]");
            }

            mappingsBag.put(mappingsName, mappingsRegistry);

            ic.pushObject(mappingsRegistry);
        } catch (Exception oops) {
            inError = true;
            addError("Could not create registry.", oops);
            throw new ActionException(oops);
        }
    }

    protected void initializeRegistry(ExceptionMappingRegistry registry) {
        List<ExceptionMapping> exceptionMappings = initialMappings();
        for (ExceptionMapping exceptionMapping : exceptionMappings) {
            registry.register(exceptionMapping);
        }
    }

    protected List<ExceptionMapping> initialMappings() {
        return Arrays.asList(
                new BeanExceptionMapping("java.lang.Exception", asList("message"), handler),

                new BeanExceptionMapping("java.nio.file.FileSystemException", asList("file", "otherFile", "reason"), handler),

                new BeanExceptionMapping("java.net.HttpRetryException", asList("responseCode", "reason", "location"), handler),
                new BeanExceptionMapping("java.net.URISyntaxException", asList("input", "reason", "index"), handler),

                new BeanExceptionMapping("java.nio.charset.IllegalCharsetNameException", asList("charsetName"), handler),

                new BeanExceptionMapping("java.sql.BatchUpdateException", asList("updateCounts"), handler),
                new BeanExceptionMapping("java.sql.SQLException", asList("errorCode", "SQLState"), handler),

                new BeanExceptionMapping("java.text.ParseException", asList("errorOffset"), handler),

                new BeanExceptionMapping("java.time.format.DateTimeParseException", asList("parsedString", "errorIndex"), handler),

                new BeanExceptionMapping("java.util.DuplicateFormatFlagsException", asList("flags"), handler),
                new BeanExceptionMapping("java.util.FormatFlagsConversionMismatchException", asList( "flags", "conversion"), handler),
                new BeanExceptionMapping("java.util.IllegalFormatCodePointException", asList( "codePoint"), handler),
                new BeanExceptionMapping("java.util.IllegalFormatConversionException", asList("conversion", "argumentClass"), handler),
                new BeanExceptionMapping("java.util.IllegalFormatFlagsException", asList("flags"), handler),
                new BeanExceptionMapping("java.util.IllegalFormatPrecisionException", asList("precision"), handler),
                new BeanExceptionMapping("java.util.IllegalFormatWidthException", asList("width"), handler),
                new BeanExceptionMapping("java.util.IllformedLocaleException", asList("errorIndex"), handler),
                new BeanExceptionMapping("java.util.InvalidPropertiesFormatException", asList("formatSpecifier"), handler),
                new BeanExceptionMapping("java.util.MissingFormatArgumentException", asList("formatSpecifier"), handler),
                new BeanExceptionMapping("java.util.MissingFormatWidthException", asList("formatSpecifier"), handler),
                new BeanExceptionMapping("java.util.MissingResourceException", asList("className", "key"), handler),
                new BeanExceptionMapping("java.util.UnknownFormatConversionException", asList("conversion"), handler),
                new BeanExceptionMapping("java.util.UnknownFormatFlagsException", asList("flags"), handler),

                new BeanExceptionMapping("javax.naming.NamingException", asList("explanation", "remainingName", "resolvedName"), handler)
        );
    }

    /**
     * Once the children elements are also parsed, now is the time to activate the
     * appender options.
     */
    public void end(InterpretationContext ec, String name) {
        if (inError) {
            return;
        }

        Object o = ec.peekObject();

        if (o != mappingsRegistry) {
            addWarn("The object at the end of the stack is not the mappingsRegistry named [" + mappingsRegistry + "] pushed earlier.");
        } else {
            ec.popObject();
        }
    }
}
