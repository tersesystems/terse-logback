package com.tersesystems.logback;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import org.xml.sax.Attributes;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class TypesafeConfigAction extends Action {
    public static final String LOGBACK = "logback";

    public static final String LOGBACK_TEST = "logback-test";

    public static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

    public static final String LOGBACK_DEBUG_PROPERTY = "terse.logback.debug";

    public static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        Config config = generateConfig(ic.getClass().getClassLoader());
        Context context = ic.getContext();
        configureContextWithConfig(context, config);
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }

    public void configureContextWithConfig(Context lc, Config config) {
        // For everything in the properties section, set it as a property.
        lc.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);
        Set<Map.Entry<String, ConfigValue>> properties = config.getConfig(ConfigConstants.PROPERTIES_KEY).entrySet();
        for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
            String key = propertyEntry.getKey();
            String value = propertyEntry.getValue().unwrapped().toString();
            lc.putProperty(key, value);
        }
    }

    public Config generateConfig(ClassLoader classLoader) {
        // Look for logback.json, logback.conf, logback.properties
        Config systemProperties = ConfigFactory.systemProperties();
        String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
        Config file = ConfigFactory.empty();
        if (fileName != null) {
            file = ConfigFactory.parseFile(new File(fileName));
        }

        Config testResources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK_TEST);
        Config resources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK);
        Config reference = ConfigFactory.parseResources(classLoader, LOGBACK_REFERENCE_CONF);

        Config config = systemProperties        // Look for a property from system properties first...
                .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
                .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
                .withFallback(resources)     // then look in logback.conf...
                .withFallback(reference)     // and then finally in logback-reference.conf.
                .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.

        // Add a check to show the config value if nothing is working...
        if (Boolean.getBoolean(LOGBACK_DEBUG_PROPERTY)) {
            String configString = config.root().render(ConfigRenderOptions.defaults());
            addInfo(configString);
        }
        return config;
    }

}
