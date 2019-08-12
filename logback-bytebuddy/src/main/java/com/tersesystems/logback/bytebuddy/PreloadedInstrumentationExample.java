package com.tersesystems.logback.bytebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use the agent loaded with
 *
 * export JAVA_TOOL_OPTIONS="-javaagent:logback-bytebuddy.jar"
 * java com.tersesystems.logback.bytebuddy.PreloadedInstrumentationExample
 */
public class PreloadedInstrumentationExample {

    public static void main(String[] args) throws Exception {
        System.currentTimeMillis();
    }
}
