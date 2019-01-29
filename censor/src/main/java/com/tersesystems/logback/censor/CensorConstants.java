package com.tersesystems.logback.censor;

public class CensorConstants {

    public static final String TYPESAFE_CONFIG_CTX_KEY = "typesafeConfig";

    public static final String CENSOR_CTX_KEY = "censor";

    public static final String CENSOR_JSON_REGEX = CENSOR_CTX_KEY + ".json.regex";
    public static final String CENSOR_JSON_REPLACEMENT = CENSOR_CTX_KEY + ".json.replacement";
    public static final String CENSOR_JSON_KEYS = CENSOR_CTX_KEY + ".json.keys";

    public static final String CENSOR_TEXT_REGEX = CENSOR_CTX_KEY + ".text.regex";
    public static final String CENSOR_TEXT_REPLACEMENT = CENSOR_CTX_KEY + ".text.replacement";
}
