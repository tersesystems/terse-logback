package com.tersesystems.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.StatusPrinter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.udojava.jmx.wrapper.JMXBeanWrapper;

import javax.management.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static com.tersesystems.logback.Constants.*;

/**
 * This class is loaded from the service loader and sets up with Typesafe Config handling
 * the logging levels and the logging appenders set through Joran (the XML configuration handler).
 */
public class TerseLogbackConfigurator extends ContextAwareBase implements Configurator {

    public static final String LOGBACK = "logback";

    public static final String LOGBACK_TEST = "logback-test";

    public static final String LOGBACK_REFERENCE_CONF = "logback-reference.conf";

    public static final String PROPERTIES_KEY = "properties";

    public static final String LOGBACK_DEBUG_PROPERTY = "terse.logback.debug";

    public static final String LOGBACK_RESOURCE_LOCATION = "/terse-logback.xml";

    public static final String LOGBACK_TEST_RESOURCE_LOCATION = "/terse-logback-test.xml";

    public static final String CONFIG_FILE_PROPERTY = "terse.logback.configurationFile";

    private boolean testMode;

    @Override
    public void configure(LoggerContext lc) {
        synchronized (lc.getConfigurationLock()) {
            try {
                addInfo("Setting up configuration at " + new java.util.Date());

                JMXBeanWrapper wrappedBean = new JMXBeanWrapper(new LogbackMXBean(lc));
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(wrappedBean, new ObjectName("com.tersesystems.logback:type=LogbackMXBean,name=Logback"));

                // Only look in terse-logback/logback.xml and logback-test.xml for the "classic" hands free setup...
                // We do not define logback-test.xml here and assume that it's completely defined with appenders etc.
                // We do allow for a "logback-test.conf" file to change settings as per normal.
                final URL testResourceUrl = getClass().getResource(LOGBACK_TEST_RESOURCE_LOCATION);
                final URL resourceUrl;
                if (testResourceUrl != null) {
                    resourceUrl = testResourceUrl;
                    testMode = true;
                    addInfo("Setting testMode = true");
                } else {
                    resourceUrl = getClass().getResource(LOGBACK_RESOURCE_LOCATION);
                    testMode = false;
                }

                // Look for logback.json, logback.conf, logback.properties
                Config systemProperties = ConfigFactory.systemProperties();
                String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
                Config file = ConfigFactory.empty();
                if (fileName != null) {
                    file = ConfigFactory.parseFile(new File(fileName));
                }

                // If we are running in test mode, we don't want the normal log level setting to pick up.
                String resourcesBaseName = (testMode) ? LOGBACK_TEST : LOGBACK;
                Config resources = ConfigFactory.parseResourcesAnySyntax(resourcesBaseName);
                Config reference = ConfigFactory.parseResources(LOGBACK_REFERENCE_CONF);

                // Put everything together, so reference has least priority...
                Config config = systemProperties.withFallback(file).withFallback(resources).withFallback(reference).resolve();

                // Add a check to show the config value if nothing is working...
                if (Boolean.getBoolean(LOGBACK_DEBUG_PROPERTY)) {
                    String configString = config.root().render(ConfigRenderOptions.defaults());
                    addInfo(configString);
                }

                // Stick the config in the logging context where it can be picked up by the SetLoggerLevelsAction
                lc.putObject(TYPESAFE_CONFIG, config);

                // For everything in the properties section, set it as a property.
                Set<Map.Entry<String, ConfigValue>> properties = config.getConfig(PROPERTIES_KEY).entrySet();
                for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
                    String key = propertyEntry.getKey();
                    String value = propertyEntry.getValue().unwrapped().toString();
                    lc.putProperty(key, value);
                }

                // And then run through with the usual XML parsing.
                ContextInitializer contextInitializer = new ContextInitializer(lc);
                contextInitializer.configureByResource(resourceUrl);
            } catch (JoranException | IntrospectionException | MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException je) {
                // status printer will show errors
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        }
    }

}
