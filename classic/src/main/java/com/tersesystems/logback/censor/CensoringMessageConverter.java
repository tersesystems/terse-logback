package com.tersesystems.logback.censor;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.ConfigConstants;
import com.typesafe.config.Config;

/**
 * Censoring message converter for text.
 *
 * Note that this does not filter out marker text or additional information related to the event,
 * i.e. it does not filter out exception text.
 */
public class CensoringMessageConverter extends ClassicConverter {

    private Censor censor;

    @Override
    public void start() {
        Config config = (Config) getContext().getObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY);
        this.censor = new RegexCensor(config, ConfigConstants.CENSOR_TEXT_REGEX, ConfigConstants.CENSOR_TEXT_REPLACEMENT);
        started = true;
    }


    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        if (censor != null) {
            return censor.apply(formattedMessage);
        } else {
            return formattedMessage;
        }
    }
}
