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
package example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Example class to show how this library works.  Please do not put your application code in with
 * your logback resource files in general, this is only an example.
 */
public class Main {

    static class Runner {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        public void doInfo() {
            logger.info("I like to do stuff");
        }

        public void doWarn() {
            logger.warn("I am a warning");
        }
    }

    public static void main(String[] args) {
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        Runner runner = new Runner();

        final ScheduledFuture<?> infoHandle = scheduler.scheduleAtFixedRate(runner::doInfo, 1, 2, SECONDS);
        final ScheduledFuture<?> warnHandle = scheduler.scheduleAtFixedRate(runner::doWarn, 1, 1, SECONDS);
        scheduler.schedule(() -> { infoHandle.cancel(true); }, 60 * 60, SECONDS);
        scheduler.schedule(() -> { warnHandle.cancel(true); }, 60 * 60, SECONDS);
    }

}
