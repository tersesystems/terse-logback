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
package com.tersesystems.logback.honeycomb.client;

public class HoneycombResponse {

    private final String reason;
    private final int status;

    public HoneycombResponse(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public boolean isSuccess() {
        return (getStatus() == 200 || getStatus() == 202);
    }

    public boolean isInvalidKey() {
        return is400() && getReason().contains("credentials");
    }

    public boolean isMalformed() {
        return is400() && getReason().contains("malformed");
    }

    public boolean isTooLarge() {
        return is400() && getReason().contains("too large");
    }

    public boolean isRateLimited() {
        return is429() && getReason().contains("rate limiting");
    }

    public boolean isBlacklisted() {
        return is429() && getReason().contains("blacklisted");
    }

    @Override
    public String toString() {
        return String.format("HoneyCombResponse(code = %s, text = %s)", getStatus(), getReason());
    }

    private boolean is400() {
        return getStatus() == 400;
    }

    private boolean is429() {
        return getStatus() == 429;
    }
}
