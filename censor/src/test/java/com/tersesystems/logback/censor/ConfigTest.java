package com.tersesystems.logback.censor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public abstract class ConfigTest {
    private static final String LOGBACK = "logback";

    private static final String LOGBACK_TEST = "logback-test";

    private static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

    public static final String LOGBACK_DEBUG_PROPERTY = "terse.logback.debug";

    private static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

    protected Config loadConfig() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Config systemProperties = ConfigFactory.systemProperties();
        String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
        Config file = ConfigFactory.empty();
        if (fileName != null) {
            file = ConfigFactory.parseFile(new File(fileName));
        }

        Config testResources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK_TEST);
        Config resources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK);
        Config reference = ConfigFactory.parseResources(classLoader, LOGBACK_REFERENCE_CONF);

        return systemProperties        // Look for a property from system properties first...
                .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
                .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
                .withFallback(resources)     // then look in logback.conf...
                .withFallback(reference)     // and then finally in logback-reference.conf.
                .resolve();
    }

}
