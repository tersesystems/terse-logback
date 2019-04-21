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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexCensor extends ContextAwareBase implements Censor, LifeCycle {

    protected volatile boolean started = false;

    private Pattern pattern = null;
    private String regex = null;

    private String replacementText;

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplacementText() {
        return replacementText;
    }

    public void setReplacementText(String replacementText) {
        this.replacementText = replacementText;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void start() {
        if (replacementText == null) {
            addError("replacementText cannot be null!");
            return;
        }

        if (regex == null) {
            addError("No regular expressions found!");
            return;
        }

        this.pattern = Pattern.compile(regex, (regex.contains("\n")) ? Pattern.MULTILINE : 0);
        this.started = true;
    }

    @Override
    public void stop() {
        this.replacementText = null;
        this.pattern = null;
        this.regex = null;
        this.started = false;
    }

    @Override
    public CharSequence censorText(CharSequence original) {
        return pattern.matcher(original).replaceAll(replacementText);
    }

}
