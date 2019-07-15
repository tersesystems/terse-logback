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
package com.tersesystems.logback.turbomarker;

import ch.qos.logback.core.spi.FilterReply;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;

import static java.util.Objects.requireNonNull;

public class LDMarkerFactory {
    private final LaunchDarklyDecider decider;

    public LDMarkerFactory(LDClientInterface client) {
        this.decider = new LaunchDarklyDecider(requireNonNull(client));
    }

    public LDMarker create(String featureFlag, LDUser user) {
        return new LDMarker(featureFlag, user, decider);
    }

    static class LaunchDarklyDecider implements MarkerContextDecider<LDUser> {
        private final LDClientInterface ldClient;

        LaunchDarklyDecider(LDClientInterface ldClient) {
            this.ldClient = ldClient;
        }

        @Override
        public FilterReply apply(ContextAwareTurboMarker<LDUser> marker, LDUser ldUser) {
            return ldClient.boolVariation(marker.getName(), ldUser, false) ?
                    FilterReply.ACCEPT :
                    FilterReply.NEUTRAL;
        }
    }

    public static class LDMarker extends ContextAwareTurboMarker<LDUser> {
        LDMarker(String name, LDUser context, ContextAwareTurboFilterDecider<LDUser> decider) {
            super(name, context, decider);
        }
    }
}
