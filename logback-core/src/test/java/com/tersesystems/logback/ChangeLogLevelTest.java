package com.tersesystems.logback;

import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeLogLevelTest {

    @Test
    public void testChangeLogLevel() {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        ChangeLogLevel changeLogLevel = new ChangeLogLevel();
        Logger logger = loggerFactory.getLogger("example");
        assertThat(logger.isTraceEnabled()).isFalse();
        changeLogLevel.changeLogLevel(logger, "TRACE");
        assertThat(logger.isTraceEnabled()).isTrue();
    }

}
