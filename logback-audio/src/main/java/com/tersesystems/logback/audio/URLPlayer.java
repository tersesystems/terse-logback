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
package com.tersesystems.logback.audio;

import ch.qos.logback.core.spi.ContextAwareBase;

import java.net.URL;

public class URLPlayer extends ContextAwareBase implements Player {

    private URL url;

    public URLPlayer() {
    }

    public void URLPlayer(URL url) {
        this.url = url;
    }

    @Override
    public void play() {
        SimplePlayer.fromURL(url).play();
    }

}
