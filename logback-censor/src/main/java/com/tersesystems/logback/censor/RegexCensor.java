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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexCensor extends ContextAwareBase implements Censor {

    protected volatile boolean started = false;

    private List<Pattern> patterns = new ArrayList<>();
    private final List<String> regexes = new ArrayList<>();

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

    public void addRegex(String regex) {
        this.regexes.add(regex);
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

        if (regexes.isEmpty()) {
            addError("No regular expressions found!");
            return;
        }

        this.patterns = regexes.stream()
                .map(rex -> Pattern.compile(rex, (rex.contains("\n")) ? Pattern.MULTILINE : 0))
                .collect(Collectors.toList());
        this.started = true;
    }

    @Override
    public void stop() {
        replacementText = null;
        this.patterns.clear();
        this.regexes.clear();
        this.started = false;
    }

    @Override
    public CharSequence censorText(CharSequence original) {
        CharSequence acc = original;
        for (Pattern pattern : patterns) {
            acc = pattern.matcher(acc).replaceAll(replacementText);
        }
        return acc;
    }

}
