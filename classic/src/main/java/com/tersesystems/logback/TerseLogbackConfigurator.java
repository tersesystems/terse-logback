package com.tersesystems.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.StatusPrinter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * This class is loaded from the service loader and sets up with Typesafe Config handling
 * the logging levels and the logging appenders set through Joran (the XML configuration handler).
 */
public class TerseLogbackConfigurator extends ContextAwareBase implements Configurator {

    public static final String LOGBACK = "logback";

    public static final String LOGBACK_TEST = "logback-test";

    public static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

    public static final String LOGBACK_DEBUG_PROPERTY = "terse.logback.debug";

    public static final String LOGBACK_RESOURCE_LOCATION = "/terse-logback.xml";

    public static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

    @Override
    public void configure(LoggerContext lc) {
        synchronized (lc.getConfigurationLock()) {
            try {
                addInfo("Setting up configuration at " + new java.util.Date());

                // Only look in logback.xml.  If you need different test appenders, then setting a "logback.mode=test" and
                // then using janino to do the set up is preferable.  Or you can use optional include files.
                URL resourceUrl = getClass().getResource(LOGBACK_RESOURCE_LOCATION);

                // Look for logback.json, logback.conf, logback.properties
                Config systemProperties = ConfigFactory.systemProperties();
                String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
                Config file = ConfigFactory.empty();
                if (fileName != null) {
                    file = ConfigFactory.parseFile(new File(fileName));
                }

                Config testResources = ConfigFactory.parseResourcesAnySyntax(LOGBACK_TEST);
                Config resources = ConfigFactory.parseResourcesAnySyntax(LOGBACK);
                Config reference = ConfigFactory.parseResources(LOGBACK_REFERENCE_CONF);

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

                // Stick the config in the logging context where it can be picked up by the SetLoggerLevelsAction
                lc.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);

                // For everything in the properties section, set it as a property.
                Set<Map.Entry<String, ConfigValue>> properties = config.getConfig(ConfigConstants.PROPERTIES_KEY).entrySet();
                for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
                    String key = propertyEntry.getKey();
                    String value = propertyEntry.getValue().unwrapped().toString();
                    lc.putProperty(key, value);
                }

                // Create the XML parser / configurator
                JoranConfigurator configurator = new JoranConfigurator() {
                    // You'd think there would be an easy way to do this, but it doesn't look like the ordering
                    // works out.  You need access to the interpreter before it parses, and it's protected.
                    @Override
                    public void addInstanceRules(RuleStore rs) {
                        super.addInstanceRules(rs);
                        rs.addRule(new ElementSelector("*/logbackMXBean"), new LogbackMXBeanAction());
                        rs.addRule(new ElementSelector("*/setLoggerLevels"), new SetLoggerLevelsAction());
                    }
                };
                configurator.setContext(lc);
                configurator.doConfigure(resourceUrl);
            } catch (Exception je) {
                // status printer will show errors
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        }
    }

}
