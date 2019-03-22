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

public class CensorConstants {

    public static final String TYPESAFE_CONFIG_CTX_KEY = "typesafeConfig";

    public static final String CENSOR_CTX_KEY = "censorText";

    public static final String CENSOR_JSON_ENABLED = CENSOR_CTX_KEY + ".json.enabled";
    public static final String CENSOR_JSON_REGEX = CENSOR_CTX_KEY + ".json.regex";
    public static final String CENSOR_JSON_REPLACEMENT = CENSOR_CTX_KEY + ".json.replacement";
    public static final String CENSOR_JSON_KEYS = CENSOR_CTX_KEY + ".json.keys";

    public static final String CENSOR_TEXT_ENABLED = CENSOR_CTX_KEY + ".text.enabled";
    public static final String CENSOR_TEXT_REGEX = CENSOR_CTX_KEY + ".text.regex";
    public static final String CENSOR_TEXT_REPLACEMENT = CENSOR_CTX_KEY + ".text.replacement";

    public static final String CENSOR_BAG = "CENSOR_BAG";
}
