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
package com.tersesystems.logback.bytebuddy;

import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;

/**
 * Borrowed from securityfixer, showing tracing when you set the security manager.
 */
public class PreloadedInstrumentationExample {

    // We don't want any ACTUAL security here when we turn on the security manager...
    static class SillyPolicy extends Policy {
        @Override
        public boolean implies(ProtectionDomain domain, Permission permission) {
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        // Programmer turns on security manager...
        Policy.setPolicy(new SillyPolicy());
        System.setSecurityManager(new SecurityManager());

        System.out.println("Security manager is set!");
        try {
            // Attacker tries to turn off security manager...
            System.setSecurityManager(null);

            // Happens on normal circumstances...
            System.err.println("ATTACK SUCCEEDED: Security manager was reset!");
        } catch (IllegalStateException e) {
            System.out.println("ATTACK FAILED: " + e.getMessage());
        }
    }

}
